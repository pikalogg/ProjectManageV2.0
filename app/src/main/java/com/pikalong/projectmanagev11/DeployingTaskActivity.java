package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pikalong.projectmanagev11.adapter.MemberBoxAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.Task;
import com.pikalong.projectmanagev11.model.User;

import java.util.ArrayList;
import java.util.List;

public class DeployingTaskActivity extends AppCompatActivity {
    private static final int PICK_IMG_FILE = 201;
    private static final int PICK_FILE = 202;
    ActionBar actionBar;

    TextView tvName, tvTime, tvDes, tvTitle;

    ListView lvAddImgFile;
    LinearLayout llAddImgFile;
    List<String> imgFiles;
    List<String> imgFileUrl;
    List<Uri> imgFilesUri;
    MemberBoxAdapter imgFileAdapter;

    ListView lvAddFile;
    LinearLayout llAddFile;
    List<String> files;
    List<String> fileUrl;
    List<Uri> filesUri;
    MemberBoxAdapter fileAdapter;

    Task mTask;
    FirebaseUser user;

    Intent mIntent;
    Project mProject;

    StorageReference storageReference;
    UploadTask uploadTask;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploying_task);

        addControl();
        addEvent();
    }
    private void addControl(){
        mIntent = getIntent();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvName = findViewById(R.id.tvName);
        tvDes = findViewById(R.id.tvDes);
        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);

        lvAddFile = findViewById(R.id.lvAddFile);
        llAddFile = findViewById(R.id.llAddFile);
        filesUri = new ArrayList<>();
        files = new ArrayList<>();
        fileUrl = new ArrayList<>();

        lvAddImgFile = findViewById(R.id.lvAddImgFile);
        llAddImgFile = findViewById(R.id.llAddImgFile);
        imgFilesUri = new ArrayList<>();
        imgFiles = new ArrayList<>();
        imgFileUrl = new ArrayList<>();


        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Tasks");
        reference.child(mIntent.getStringExtra("taskId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTask = dataSnapshot.getValue(Task.class);

                tvName.setText(mTask.getLeadName());
                tvTime.setText(mTask.getTimestamp());
                tvTitle.setText(mTask.getTitle());
                tvDes.setText(mTask.getDes());

                DatabaseReference referencePro = firebaseDatabase.getReference("Projects");
                referencePro.child(mTask.getProjectId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProject = dataSnapshot.getValue(Project.class);

                        actionBar.setTitle(mProject.getTitle());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
    }
    private void addEvent(){
        llAddImgFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMG_FILE);
            }
        });

        llAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf|image/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri tmpUri = data.getData();
            String file = getFileName(tmpUri);
            if(imgFiles.contains(file)){
                Toast.makeText(this, "ảnh đã được chọn", Toast.LENGTH_LONG).show();
                return;
            }

            imgFiles.add(file);
            imgFileUrl.add("");
            imgFilesUri.add(tmpUri);
            imgFileAdapter = new MemberBoxAdapter(imgFiles, getApplicationContext());
            lvAddImgFile.setAdapter(imgFileAdapter);

            lvAddImgFile.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri tmpUri = data.getData();
            String file = getFileName(tmpUri);
            if(files.contains(file)){
                Toast.makeText(this, "File đã được chọn", Toast.LENGTH_LONG).show();
                return;
            }
            filesUri.add(tmpUri);
            files.add(file);
            fileUrl.add("");
            fileAdapter = new MemberBoxAdapter(files, getApplicationContext());
            lvAddFile.setAdapter(fileAdapter);

            lvAddFile.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed(); //nut quay lai cua dien thoai
                return true;

            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    //nut back dt
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /// name file in uri
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /// get .(file)
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    ///////////////////////////////
    private String listToString(List<String> mLists){
        String conten = "";
        for (String str : mLists){
            conten += str + "|";
        }
        return  conten;
    }

    private  List<String> stringToList(String mStr){
        List<String> mLists = new ArrayList<>();
        String[] strs = mStr.split("\\|");
        for (String str : strs){
            if(!str.equals(""))
                mLists.add(str);
        }
        return  mLists;
    }
}
