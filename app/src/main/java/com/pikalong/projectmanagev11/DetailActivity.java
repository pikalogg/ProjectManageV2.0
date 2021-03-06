package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pikalong.projectmanagev11.adapter.FileAdapte;
import com.pikalong.projectmanagev11.adapter.ImageAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    ActionBar actionBar;
    Intent intent;

    TextView tvTitle, tvDes;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    StorageReference reference;

    RecyclerView reAddImgFile;
    List<String> imgFileUrl;
    ImageAdapter imageAdapter;

    ListView lvAddFile;
    List<String> filesUrl;
    FileAdapte fileAdapte;

    Project mProject;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        addControl();
        addEvent();
    }





    private void addControl(){
        intent = getIntent();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Uploads");


        reAddImgFile = findViewById(R.id.reAddImgFile);
        imgFileUrl = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imgFileUrl);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        reAddImgFile.setLayoutManager(layoutManager);


        lvAddFile = findViewById(R.id.lvAddFile);
        filesUrl = new ArrayList<>();

        fileAdapte = new FileAdapte(getApplicationContext(), filesUrl);


        tvTitle = findViewById(R.id.tvTitle);
        tvDes = findViewById(R.id.tvDes);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Projects");
        databaseReference.child(intent.getStringExtra("projectId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProject = dataSnapshot.getValue(Project.class);
//                Toast.makeText(getBaseContext(), mTask.getDes(), Toast.LENGTH_LONG).show();

                tvTitle.setText(mProject.getTitle());
                tvDes.setText(mProject.getDes());

                List<String> filesTmp = stringToList(mProject.getFiles());
                List<String> impsTamp = stringToList(mProject.getImgFiles());

                for(String fileTmp : filesTmp){
                    filesUrl.add(fileTmp);
                }

                for (String imgTmp : impsTamp){
                    imgFileUrl.add(imgTmp);
                }
                reAddImgFile.setAdapter(imageAdapter);
                lvAddFile.setAdapter(fileAdapte);


                if (filesUrl.size()==0){
                    lvAddFile.setVisibility(View.GONE);
                }
                if (imgFileUrl.size()==0){
                    reAddImgFile.setVisibility(View.GONE);
                }

                actionBar.setTitle(mProject.getTitle());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addEvent(){
        lvAddFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dowload(filesUrl.get(i));
            }
        });
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
//        super.onBackPressed();
        finish();
    }

    /////////////////////////////////////
    private void dowload(final String child){
        reference = storageReference.child(child);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                dowloadFile(DetailActivity.this, child, Environment.DIRECTORY_DOWNLOADS, url);
            }
        });



    }

    private void dowloadFile(Context context, String nameFile, String destinationDirectoyr , String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectoyr, nameFile);

        downloadManager.enqueue(request);

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
