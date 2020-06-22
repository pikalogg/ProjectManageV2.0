package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.User;

import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddTaskActivity extends AppCompatActivity {
    ActionBar actionBar;

    TextView tvMem;
    EditText edTitle, edDes;
    Button btnAddTask;

    RelativeLayout rlRoot;
    LinearLayout llAddMem;
    ListView lvAddMem;

    SweetAlertDialog sweetAlertDialog;

    List<String> listMem;
    List<String> listMemId;
    ArrayAdapter<String> adapter;


    String usIdSelect = "";
    User mUser;
    FirebaseUser user;

    Intent mIntent;
    Project mProject;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        addControl();
        addEvent();
        addData();
    }

    private void addControl(){
        actionBar = getSupportActionBar();
        actionBar.setTitle("Tạo công việc mới");
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvMem = findViewById(R.id.tvMem);
        edTitle = findViewById(R.id.edTitle);
        edDes = findViewById(R.id.edDes);
        btnAddTask = findViewById(R.id.btnAddTask);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);


        rlRoot = findViewById(R.id.rlRoot);
        llAddMem = findViewById(R.id.llAddMem);
        lvAddMem = findViewById(R.id.lvAddMem);

        listMem = new ArrayList<>();
        listMemId = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,
                R.layout.item,
                R.id.cheese_name,
                listMem
        );
        lvAddMem.setAdapter(adapter);

        mIntent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Projects");
        reference.child(mIntent.getStringExtra("projectId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProject = dataSnapshot.getValue(Project.class);
                String usId = mProject.getUsId();
                listMemId = stringToList(usId);
                listMemId.add(mProject.getUid());
                for (String ttt : listMemId) listMem.add(ttt);

                DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
                for (int i = 0; i < listMemId.size(); i++){
                    String memId = listMemId.get(i);
                    final int finalI = i;
                    referenceUser.child(memId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User tmpUser = dataSnapshot.getValue(User.class);
                            listMem.set(finalI,tmpUser.getName());
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void addEvent(){
        rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAddMem.setVisibility(View.GONE);
            }
        });
        tvMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAddMem.setVisibility(View.VISIBLE);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addTask();
            }
        });




        lvAddMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvMem.setText(listMem.get(i));
                usIdSelect = listMemId.get(i);
                llAddMem.setVisibility(View.GONE);
            }
        });

    }

    private  void addData(){
        String uid = user.getUid();
        DatabaseReference reference = firebaseDatabase.getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
//        Intent intent = new Intent(AddTaskActivity.this, ProjectActivity.class);
//        startActivity(intent);
        finish();
    }
    //////////////////////////////////////
    private void addTask(){
        String title = edTitle.getText().toString();
        String des = edDes.getText().toString();

        if(title.equals("")){
            edTitle.setError("Không được để trống tiêu đề");
            edTitle.setFocusable(true);
            return;
        }
        if(des.equals("")){
            edDes.setError("Không được để trống nội dung");
            edDes.setFocusable(true);
            return;
        }
        if (!title.equals("") && !des.equals(""))
        {
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uId", user.getUid());
            hashMap.put("usId", usIdSelect);
            hashMap.put("projectId", mProject.getId());
            hashMap.put("title", title);
            hashMap.put("des", des);
            hashMap.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
            hashMap.put("image", "https://");
            hashMap.put("files", "");
            hashMap.put("imgFiles", ""); //lấy sau

            if(usIdSelect.equals("")){
                hashMap.put("leadName", "Chưa giao việc");
                hashMap.put("status", 0);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference reference = firebaseDatabase.getReference("Tasks");
                String key = reference.push().getKey();
                hashMap.put("id", key);
                reference.child(key).setValue(hashMap);

                HashMap<String, Object> hashMapPro = new HashMap<>();
                hashMapPro.put("tasksId", mProject.getTasksId() + "|" + key);
                DatabaseReference referencePro = firebaseDatabase.getReference("Projects");
                referencePro.child(mProject.getId()).updateChildren(hashMapPro);

                finish();
            } else {
                DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
                referenceUser.child(usIdSelect).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nameT = dataSnapshot.getValue(User.class).getName();
                        hashMap.put("leadName", nameT);
                        hashMap.put("status", 1);


                        DatabaseReference reference = firebaseDatabase.getReference("Tasks");
                        String key = reference.push().getKey();
                        hashMap.put("id", key);
                        reference.child(key).setValue(hashMap);

                        HashMap<String, Object> hashMapPro = new HashMap<>();
                        hashMapPro.put("tasksId", mProject.getTasksId() + "|" + key);
                        DatabaseReference referencePro = firebaseDatabase.getReference("Projects");
                        referencePro.child(mProject.getId()).updateChildren(hashMapPro);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

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
