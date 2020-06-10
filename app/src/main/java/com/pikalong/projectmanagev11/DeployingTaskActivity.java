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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeployingTaskActivity extends AppCompatActivity {
    private static final int PICK_IMG_FILE = 201;
    private static final int PICK_FILE = 202;
    ActionBar actionBar;

    TextView tvName, tvTime, tvDes, tvTitle;
    Button btnDep;

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
    SweetAlertDialog sweetAlertDialog;

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

        btnDep = findViewById(R.id.btnDep);

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
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);;

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

        lvAddFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                files.remove(i);
                filesUri.remove(i);
                fileUrl.remove(i);
                fileAdapter.notifyDataSetChanged();

                if (files.size() == 0) {
                    lvAddFile.setVisibility(View.GONE);
                }
            }
        });

        lvAddImgFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                imgFiles.remove(i);
                imgFilesUri.remove(i);
                imgFileUrl.remove(i);
                imgFileAdapter.notifyDataSetChanged();

                if (imgFiles.size() == 0) {
                    lvAddImgFile.setVisibility(View.GONE);
                }
            }
        });

        btnDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depFun();
            }
        });
    }

    private void depFun(){

        sweetAlertDialog.setTitle("Đang tải dữ liệu lên");
        sweetAlertDialog.setContentText("Hãy chờ...");
        sweetAlertDialog.show();

        final List<String> files = new ArrayList<>();
        final List<String> imgFiles = new ArrayList<>();

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", 2);


        if (filesUri.size()==0){
            if(imgFilesUri.size()==0){
                // khong dang gi
//                hashMap.put("files", "");
//                hashMap.put("imgFiles", "");
                reference.child(mTask.getId()).updateChildren(hashMap);

                sweetAlertDialog.dismiss();
                finish();
            } else {
                // dang anh ma khong dang file

                for (int i=0;i<imgFilesUri.size();i++){
                    Uri imgUri = imgFilesUri.get(i);
                    final String tmpImgUrl = System.currentTimeMillis() + getFileName(imgUri);
                    final StorageReference fileReference = storageReference.child(tmpImgUrl);
                    final int finalI = i;
                    uploadTask = (UploadTask) fileReference.putFile(imgUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imgFiles.add(tmpImgUrl);

                                    if (finalI == imgFilesUri.size()-1){
                                        hashMap.put("imgFiles", listToString(imgFiles));
                                        reference.child(mTask.getId()).updateChildren(hashMap);

                                        sweetAlertDialog.dismiss();
                                        finish();
                                    }

                                }
                            });
                }

            }

        }else{
            //dang file
            for (int indexFileUri = 0; indexFileUri < filesUri.size(); indexFileUri++){
                Uri fileUri = filesUri.get(indexFileUri);
                //13 chu so + ten
                final String tmpFileUrl = System.currentTimeMillis() + getFileName(fileUri);
                final StorageReference fileReference = storageReference.child(tmpFileUrl);
                final int finalIndex = indexFileUri;
                uploadTask = (UploadTask) fileReference.putFile(fileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                files.add(tmpFileUrl);

                                if (finalIndex == filesUri.size()-1){
                                    hashMap.put("files", listToString(files));

                                    if(imgFilesUri.size()==0){
                                        // dang file ma khong dang anh
                                        reference.child(mTask.getId()).updateChildren(hashMap);
                                        sweetAlertDialog.dismiss();
                                        finish();
                                    } else {
                                        // dang ca hai
                                        for (int i=0;i<imgFilesUri.size();i++){
                                            Uri imgUri = imgFilesUri.get(i);
                                            final String tmpImgUrl = System.currentTimeMillis() + getFileName(imgUri);
                                            final StorageReference fileReference = storageReference.child(tmpImgUrl);
                                            final int finalI = i;
                                            uploadTask = (UploadTask) fileReference.putFile(imgUri)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            imgFiles.add(tmpImgUrl);

                                                            if (finalI == imgFilesUri.size()-1){
                                                                hashMap.put("imgFiles", listToString(imgFiles));
                                                                reference.child(mTask.getId()).updateChildren(hashMap);

                                                                sweetAlertDialog.dismiss();
                                                                finish();
                                                            }

                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        });
            }

        }
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
