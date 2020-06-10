package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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
import com.pikalong.projectmanagev11.adapter.ProjectAdapter;
import com.pikalong.projectmanagev11.adapter.TaskAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.Task;
import com.pikalong.projectmanagev11.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProjectActivity extends AppCompatActivity {
    private static final int PICK_IMG_FILE = 301;
    ActionBar actionBar;

    int in = 0;
    ListView listView;
    TextView btn_gv, btn_dl, btn_kt, btn_ht;
    LinearLayout l_tmp, l_tmp2;

    List<Task> tasks_gv;
    List<Task> tasks_dl;
    List<Task> tasks_kt;
    List<Task> tasks_ht;
    TaskAdapter taskAdapter_gv;
    TaskAdapter taskAdapter_dl;
    TaskAdapter taskAdapter_kt;
    TaskAdapter taskAdapter_ht;

    SweetAlertDialog sweetAlertDialog;

    ImageButton btn_add;

    Project mProject;
//    User mUser, leadUser;
    FirebaseUser user;

    Intent mIntent;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

    }

    private void addControl(){
        tasks_gv = new ArrayList<>();
        tasks_dl = new ArrayList<>();
        tasks_kt = new ArrayList<>();
        tasks_ht = new ArrayList<>();

        taskAdapter_gv = new TaskAdapter(tasks_gv, getApplicationContext());
        taskAdapter_dl = new TaskAdapter(tasks_dl, getApplicationContext());
        taskAdapter_kt = new TaskAdapter(tasks_kt, getApplicationContext());
        taskAdapter_ht = new TaskAdapter(tasks_ht, getApplicationContext());


        listView = findViewById(R.id.lv_conten);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_gv = findViewById(R.id.btn_giaoviec);
        btn_dl = findViewById(R.id.btn_danglam);
        btn_kt = findViewById(R.id.btn_kiemtra);
        btn_ht = findViewById(R.id.btn_hoanthanh);

        l_tmp = findViewById(R.id.l_tmp);
        l_tmp2 = findViewById(R.id.l_tmp2);

        btn_add = findViewById(R.id.btn_add);

        mIntent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Projects").child(mIntent.getStringExtra("id"));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProject = dataSnapshot.getValue(Project.class);

                actionBar.setTitle(mProject.getTitle());
                if(!user.getUid().equals(mProject.getUid()) || mProject.getStatus() == 1){
                    btn_add.setVisibility(View.GONE);
                }

                String tasksId = mProject.getTasksId();
                final List<String> listTasks = stringToList(tasksId);

//                Toast.makeText(getBaseContext(), listTasks.size() + "", Toast.LENGTH_LONG).show();
                DatabaseReference referenceTask = firebaseDatabase.getReference("Tasks");

                for (int i = 0 ; i < listTasks.size(); i++){
                    String taskId = listTasks.get(i);
                    final int finalI = i;
                    referenceTask.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Task taskTmp = dataSnapshot.getValue(Task.class);
                            if (taskTmp.getStatus() == 0) {
                                if(mProject.getUid().equals(user.getUid())){
                                    tasks_gv.add(taskTmp);
                                    taskAdapter_gv.notifyDataSetChanged();
                                }
                            }
                            if (taskTmp.getStatus() == 1) {
                                tasks_dl.add(taskTmp);
                                taskAdapter_dl.notifyDataSetChanged();
                            }
                            if (taskTmp.getStatus() == 2) {
                                if (mProject.getUid().equals(user.getUid())){
                                    tasks_kt.add(taskTmp);
                                    taskAdapter_kt.notifyDataSetChanged();
                                }
                            }
                            if (taskTmp.getStatus() == 3) {
                                tasks_ht.add(taskTmp);
                                taskAdapter_ht.notifyDataSetChanged();
                            }

                            if(finalI == listTasks.size()-1){
                                if(in == 0) listView.setAdapter(taskAdapter_gv);
                                if(in == 1) listView.setAdapter(taskAdapter_dl);
                                if(in == 2) listView.setAdapter(taskAdapter_kt);
                                if(in == 3) listView.setAdapter(taskAdapter_ht);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

//                DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
//                referenceUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        mUser = dataSnapshot.getValue(User.class);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                referenceUser.child(mProject.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        leadUser = dataSnapshot.getValue(User.class);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void addEvent(){
        btn_gv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_gv);
                l_tmp.setGravity(Gravity.LEFT);
                l_tmp2.setGravity(Gravity.LEFT);
                in = 0;
            }
        });
        btn_dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_dl);
                l_tmp.setGravity(Gravity.LEFT);
                l_tmp2.setGravity(Gravity.RIGHT);
                in = 1;
            }
        });
        btn_kt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_kt);
                l_tmp.setGravity(Gravity.RIGHT);
                l_tmp2.setGravity(Gravity.LEFT);
                in = 2;
            }
        });
        btn_ht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_ht);
                l_tmp.setGravity(Gravity.RIGHT);
                l_tmp2.setGravity(Gravity.RIGHT);
                in = 3;
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, AddTaskActivity.class);
                intent.putExtra("projectId", mProject.getId());
                startActivity(intent);
//                finish();
            }
        });


        /////////////////
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mProject.getStatus() == 0){
                    Intent myIntent = null;
                    if(in==0){
                        myIntent = new Intent(ProjectActivity.this, GiveTaskActivity.class);
                        myIntent.putExtra("taskId", tasks_gv.get(i).getId());
                        startActivity(myIntent);
                    } else if (in ==1 ){
                        myIntent = new Intent(ProjectActivity.this, DeployingTaskActivity.class);
                        myIntent.putExtra("taskId", tasks_dl.get(i).getId());

                        if (!user.getUid().equals(tasks_dl.get(i).getUsId())) {
                            Toast.makeText(getApplicationContext(), "Đây không phải công việc dành cho bạn", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(myIntent);
                        }
                    } else if (in ==2 ){
                        myIntent = new Intent(ProjectActivity.this, InspectTaskActivity.class);
                        myIntent.putExtra("taskId", tasks_kt.get(i).getId());
                        startActivity(myIntent);
                    } else if (in == 3){
                        myIntent = new Intent(ProjectActivity.this, SuccessfulTaskActivity.class);
                        myIntent.putExtra("taskId", tasks_ht.get(i).getId());
                        startActivity(myIntent);
                    }
                }

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
            sweetAlertDialog.setTitleText("Đang lưu ảnh");
            sweetAlertDialog.setContentText("Chờ.....");
            sweetAlertDialog.show();

            Uri tmpUri = data.getData();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference("Uploads");
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(tmpUri));
            UploadTask uploadTask = (UploadTask) fileReference.putFile(tmpUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("image", downloadUri.toString());

                            reference.updateChildren(hashMap);

                            sweetAlertDialog.dismiss();
                        }
                    });
                }
            });



        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(user.getUid().equals(mIntent.getStringExtra("leadId"))){
            if(mIntent.getIntExtra("status", 0) == 0){
                getMenuInflater().inflate(R.menu.menu_pro_leader, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_pro_leader_kt, menu);
            }
        }
        else {
            getMenuInflater().inflate(R.menu.menu_pro_member, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        HashMap<String, Object> hashMapPro = new HashMap<>();
        DatabaseReference referencePro = firebaseDatabase.getReference("Projects");

        Intent intentT;

        //noinspection SimplifiableIfStatement
        switch(id){
            case android.R.id.home:
                onBackPressed(); //nut quay lai cua dien thoai
                return true;
                ///////////////// action #

            case R.id.action_return:
                hashMapPro.put("status", 1);
                referencePro.child(mProject.getId()).updateChildren(hashMapPro);

                finish();
                return true;

            case R.id.action_continue:
                hashMapPro.put("status", 0);
                referencePro.child(mProject.getId()).updateChildren(hashMapPro);

                finish();
                return true;

            case R.id.action_changeImg:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMG_FILE);
                break;
            case R.id.action_detail:
                Intent intent1 = new Intent(ProjectActivity.this, DetailActivity.class);
                intent1.putExtra("projectId", mProject.getId());
                startActivity(intent1);
                break;
            case R.id.action_messenger:
                Intent intent2 = new Intent(ProjectActivity.this, MessengerActivity.class);
                intent2.putExtra("projectId", mProject.getId());
                startActivity(intent2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //nut back dt
    @Override
    public void onBackPressed() {
//        Intent intentT = new Intent(ProjectActivity.this, DashboardActivity.class);
//        startActivity(intentT);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        addControl();
        addEvent();
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
