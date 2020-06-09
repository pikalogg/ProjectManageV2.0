package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pikalong.projectmanagev11.adapter.MemberBoxAdapter;
import com.pikalong.projectmanagev11.adapter.ProjectAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.User;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddProjectActivity extends AppCompatActivity {
    private static final int PICK_IMG_FILE = 101;
    private static final int PICK_FILE = 102;
    ActionBar actionBar;

    ListView lvAddMem;
    LinearLayout llAddMem;
    List<String> members;
    List<String> memberUid;
    MemberBoxAdapter memberBoxAdapter;

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

    String proName = "";
    String proDes = "";


    Button btnAddPro;
    boolean checkMem = false;
    String tmpUid;
    String keyPro = "";

    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    UploadTask uploadTask;

    EditText edNamePro, edDesPro;

    SweetAlertDialog sweetAlertDialog;

    int gd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        addControl();
        addEvent();
        addData();
    }

    private void addControl(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Tạo dự án mới");
        actionBar.setDisplayHomeAsUpEnabled(true);


        lvAddMem = findViewById(R.id.lvAddMem);
        llAddMem =findViewById(R.id.llAddMem);

        memberUid = new ArrayList<>();
        members = new ArrayList<>();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

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

        edDesPro = findViewById(R.id.edDesPro);
        edNamePro = findViewById(R.id.edNamePro);

        btnAddPro = findViewById(R.id.btnAddPro);


        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
    private void addEvent(){
        llAddMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AddProjectActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_mem, null);

                final EditText edMemId = mView.findViewById(R.id.edMemId);
                final TextView tvMemName = mView.findViewById(R.id.tvMemName);
                Button btnCheck = mView.findViewById(R.id.btnCheck);
                Button btnAdd = mView.findViewById(R.id.btnAdd);
                Button btnCancel = mView.findViewById(R.id.btnCancel);

                alert.setView(mView);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                    }
                });

                btnCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvMemName.setText("");
                        final String uid = edMemId.getText().toString();
                        tmpUid = uid;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = reference.child(uid);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChildren()){
                                    User tuser = dataSnapshot.getValue(User.class);
                                    if(user.getUid().equals(tuser.getUid())){
                                        Toast.makeText(getBaseContext(), "Bạn mặc định là nhóm trưởng của dự án này", Toast.LENGTH_LONG).show();
                                        checkMem = false;
                                        return;
                                    }
                                    checkMem = true;
                                    tvMemName.setText(tuser.getName());
                                }
                                else {
                                    checkMem = false;
                                    edMemId.setError("Không tìm thấy");
                                    edMemId.setFocusable(true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkMem){
                            if(memberUid.indexOf(tmpUid) < 0){
                                members.add(tvMemName.getText().toString());
                                memberBoxAdapter = new MemberBoxAdapter(members, getApplicationContext());
                                lvAddMem.setAdapter(memberBoxAdapter);

                                memberUid.add(new String(tmpUid));

                                lvAddMem.setVisibility(View.VISIBLE);

                                alertDialog.dismiss();
                            }
                            else{
                                Toast.makeText(AddProjectActivity.this, "Thành viên đã được thêm trước đó", Toast.LENGTH_LONG).show();
                                edMemId.setFocusable(true);
                            }
                        }
                        else {
                            Toast.makeText(AddProjectActivity.this, "Chưa chọn thành viên", Toast.LENGTH_LONG).show();
                            edMemId.setFocusable(true);
                        }
                    }
                });
                alertDialog.show();
            }
        });

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
        btnAddPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPro();
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

    private  void addData(){
        memberBoxAdapter = new MemberBoxAdapter(members, getApplicationContext());
        lvAddMem.setAdapter(memberBoxAdapter);

        lvAddMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                members.remove(i);
                memberUid.remove(i);
                memberBoxAdapter.notifyDataSetChanged();

                if (members.size() == 0) {
                    lvAddMem.setVisibility(View.GONE);
                }
            }
        });

        imgFileAdapter = new MemberBoxAdapter(imgFiles, getApplicationContext());
        lvAddImgFile.setAdapter(imgFileAdapter);

        lvAddImgFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                imgFiles.remove(i);
                imgFileUrl.remove(i);
                imgFileAdapter.notifyDataSetChanged();

                if (imgFiles.size() == 0) {
                    lvAddImgFile.setVisibility(View.GONE);
                }
            }
        });

        fileAdapter = new MemberBoxAdapter(files, getApplicationContext());
        lvAddFile.setAdapter(fileAdapter);

        lvAddFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                files.remove(i);
                fileUrl.remove(i);
                fileAdapter.notifyDataSetChanged();

                if (files.size() == 0) {
                    lvAddFile.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case android.R.id.home:
                onBackPressed(); //nut quay lai cua dien thoai
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //nut back dt
    @Override
    public void onBackPressed() {

//        Intent intent = new Intent(AddProjectActivity.this, DashboardActivity.class);
//        startActivity(intent);
        finish();
    }

    //////////////////////////////////////////////////////////
    private void addPro(){

        proName = edNamePro.getText().toString();
        proDes = edDesPro.getText().toString();

        if(proName.equals("")){
            edNamePro.setError("Tên dự án không được để trống!");
            edNamePro.setFocusable(true);
            return;
        }
        sweetAlertDialog.setTitle("Đang tạo project");
        sweetAlertDialog.setContentText("Hãy chờ...");
        sweetAlertDialog.show();



        uploadFile();
    }

    private void uploadFile(){
        if(imgFilesUri.size()==0){
            if(filesUri.size()==0){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uId", user.getUid());
                hashMap.put("usId", listToString(memberUid));
                hashMap.put("tasksId", "");
                hashMap.put("title", proName);
                hashMap.put("des", proDes);
                hashMap.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
                hashMap.put("image", "https://");
                hashMap.put("files", listToString(fileUrl));
                hashMap.put("imgFiles", listToString(imgFileUrl)); //lấy sau
                hashMap.put("status", 0);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference reference = firebaseDatabase.getReference("Projects");
                keyPro = reference.push().getKey();
                hashMap.put("id", keyPro);
                reference.child(keyPro).setValue(hashMap);

                //up uID
                final DatabaseReference referenceU = firebaseDatabase.getReference("Users");
                referenceU.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User mUser = dataSnapshot.getValue(User.class);
                        HashMap<String, Object> hashMapU = new HashMap<>();
                        hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);
                        referenceU.child(user.getUid()).updateChildren(hashMapU);
                        // up usID
                        if (memberUid.size() == 0){
                            sweetAlertDialog.dismiss();
                            finish();
                        } else {
                            for (int k = 0; k< memberUid.size();k++){
                                final String uId = memberUid.get(k);
                                final int finalK = k;
                                referenceU.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User mUser = dataSnapshot.getValue(User.class);

                                        HashMap<String, Object> hashMapU = new HashMap<>();
                                        hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);

                                        referenceU.child(uId).updateChildren(hashMapU);

                                        if(finalK == memberUid.size()-1){
                                            sweetAlertDialog.dismiss();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
            else {
                for(int j =0;j< filesUri.size();j++){
                    Uri urifile = filesUri.get(j);
                    if(urifile != null){
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(urifile));
                        final int finalJ = j;
                        uploadTask = (UploadTask) fileReference.putFile(urifile)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final Uri downloadUrl = uri;

                                                fileUrl.add(downloadUrl.toString());
                                                if (finalJ == filesUri.size()-1){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("uId", user.getUid());
                                                    hashMap.put("usId", listToString(memberUid));
                                                    hashMap.put("tasksId", "");
                                                    hashMap.put("title", proName);
                                                    hashMap.put("des", proDes);
                                                    hashMap.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
                                                    hashMap.put("image", "https://");
                                                    hashMap.put("files", listToString(fileUrl));
                                                    hashMap.put("imgFiles", listToString(imgFileUrl)); //lấy sau
                                                    hashMap.put("status", 0);

                                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                    DatabaseReference reference = firebaseDatabase.getReference("Projects");
                                                    keyPro = reference.push().getKey();
                                                    hashMap.put("id", keyPro);
                                                    reference.child(keyPro).setValue(hashMap);

                                                    final DatabaseReference referenceU = firebaseDatabase.getReference("Users");
                                                    referenceU.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            User mUser = dataSnapshot.getValue(User.class);
                                                            HashMap<String, Object> hashMapU = new HashMap<>();
                                                            hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);
                                                            referenceU.child(user.getUid()).updateChildren(hashMapU);
                                                            // up usID
                                                            if (memberUid.size() == 0){
                                                                sweetAlertDialog.dismiss();
                                                                finish();
                                                            } else {
                                                                for (int k = 0; k< memberUid.size();k++){
                                                                    final String uId = memberUid.get(k);
                                                                    final int finalK = k;
                                                                    referenceU.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            User mUser = dataSnapshot.getValue(User.class);

                                                                            HashMap<String, Object> hashMapU = new HashMap<>();
                                                                            hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);

                                                                            referenceU.child(uId).updateChildren(hashMapU);

                                                                            if(finalK == memberUid.size()-1){
                                                                                sweetAlertDialog.dismiss();
                                                                                finish();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                }
            }
        }
        else {
            //up img
            for(int i =0;i<imgFilesUri.size();i++){
                Uri uriImg = imgFilesUri.get(i);
                if(uriImg != null){
                    final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uriImg));
                    final int finalI = i;
                    uploadTask = (UploadTask) fileReference.putFile(uriImg)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final Uri downloadUrl = uri;
                                            imgFileUrl.add(downloadUrl.toString());
                                            if (finalI == imgFilesUri.size() - 1){
                                                //upfile
                                                if (filesUri.size()==0){
                                                    if(filesUri.size()==0){
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("uId", user.getUid());
                                                        hashMap.put("usId", listToString(memberUid));
                                                        hashMap.put("tasksId", "");
                                                        hashMap.put("title", proName);
                                                        hashMap.put("des", proDes);
                                                        hashMap.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
                                                        hashMap.put("image", "https://");
                                                        hashMap.put("files", listToString(fileUrl));
                                                        hashMap.put("imgFiles", listToString(imgFileUrl)); //lấy sau
                                                        hashMap.put("status", 0);

                                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                        DatabaseReference reference = firebaseDatabase.getReference("Projects");
                                                        keyPro = reference.push().getKey();
                                                        hashMap.put("id", keyPro);
                                                        reference.child(keyPro).setValue(hashMap);

                                                        final DatabaseReference referenceU = firebaseDatabase.getReference("Users");
                                                        referenceU.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                User mUser = dataSnapshot.getValue(User.class);
                                                                HashMap<String, Object> hashMapU = new HashMap<>();
                                                                hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);
                                                                referenceU.child(user.getUid()).updateChildren(hashMapU);
                                                                // up usID
                                                                if (memberUid.size() == 0){
                                                                    sweetAlertDialog.dismiss();
                                                                    finish();
                                                                } else {
                                                                    for (int k = 0; k< memberUid.size();k++){
                                                                        final String uId = memberUid.get(k);
                                                                        final int finalK = k;
                                                                        referenceU.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                User mUser = dataSnapshot.getValue(User.class);

                                                                                HashMap<String, Object> hashMapU = new HashMap<>();
                                                                                hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);

                                                                                referenceU.child(uId).updateChildren(hashMapU);

                                                                                if(finalK == memberUid.size()-1){
                                                                                    sweetAlertDialog.dismiss();
                                                                                    finish();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                } else {
                                                        for(int j =0;j< filesUri.size();j++){
                                                            Uri urifile = filesUri.get(j);
                                                            if(urifile != null){
                                                                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(urifile));
                                                                final int finalJ = j;
                                                                uploadTask = (UploadTask) fileReference.putFile(urifile)
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri) {
                                                                                        final Uri downloadUrl = uri;

                                                                                        fileUrl.add(downloadUrl.toString());
                                                                                        if (finalJ == filesUri.size()-1){
                                                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                                                            hashMap.put("uId", user.getUid());
                                                                                            hashMap.put("usId", listToString(memberUid));
                                                                                            hashMap.put("tasksId", "");
                                                                                            hashMap.put("title", proName);
                                                                                            hashMap.put("des", proDes);
                                                                                            hashMap.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
                                                                                            hashMap.put("image", "https://");
                                                                                            hashMap.put("files", listToString(fileUrl));
                                                                                            hashMap.put("imgFiles", listToString(imgFileUrl)); //lấy sau
                                                                                            hashMap.put("status", 0);

                                                                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                                            DatabaseReference reference = firebaseDatabase.getReference("Projects");
                                                                                            keyPro = reference.push().getKey();
                                                                                            hashMap.put("id", keyPro);
                                                                                            reference.child(keyPro).setValue(hashMap);

                                                                                            final DatabaseReference referenceU = firebaseDatabase.getReference("Users");
                                                                                            referenceU.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    User mUser = dataSnapshot.getValue(User.class);
                                                                                                    HashMap<String, Object> hashMapU = new HashMap<>();
                                                                                                    hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);
                                                                                                    referenceU.child(user.getUid()).updateChildren(hashMapU);
                                                                                                    // up usID
                                                                                                    if (memberUid.size() == 0){
                                                                                                        sweetAlertDialog.dismiss();
                                                                                                        finish();
                                                                                                    } else {
                                                                                                        for (int k = 0; k< memberUid.size();k++){
                                                                                                            final String uId = memberUid.get(k);
                                                                                                            final int finalK = k;
                                                                                                            referenceU.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                    User mUser = dataSnapshot.getValue(User.class);

                                                                                                                    HashMap<String, Object> hashMapU = new HashMap<>();
                                                                                                                    hashMapU.put("projects", mUser.getProjects() + "|" + keyPro);

                                                                                                                    referenceU.child(uId).updateChildren(hashMapU);

                                                                                                                    if(finalK == memberUid.size()-1){
                                                                                                                        sweetAlertDialog.dismiss();
                                                                                                                        finish();
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }

                                                }

                                            }
                                        }
                                    });
                                }
                            });
                }
            }
        }

    }






















    ///////////////////////////////////////////////

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
