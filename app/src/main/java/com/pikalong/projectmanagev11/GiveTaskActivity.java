package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.pikalong.projectmanagev11.model.Task;
import com.pikalong.projectmanagev11.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GiveTaskActivity extends AppCompatActivity {
    ActionBar actionBar;

    TextView tvMem, tvTime;
    EditText edTitle, edDes;
    Button btnGive;

    RelativeLayout rlRoot;
    LinearLayout llAddMem;
    ListView lvAddMem;

    SweetAlertDialog sweetAlertDialog;

    List<String> listMem;
    List<String> listMemId;
    ArrayAdapter<String> adapter;


    String usIdSelect = "";
    Task mTask;
    FirebaseUser user;

    Intent mIntent;
    Project mProject;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_task);
        addControl();
        addEvent();
    }
    private void addControl(){
        mIntent = getIntent();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvMem = findViewById(R.id.tvMem);
        edTitle = findViewById(R.id.edTitle);
        edDes = findViewById(R.id.edDes);
        btnGive = findViewById(R.id.btnGive);
        tvTime = findViewById(R.id.tvTime);

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

        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Tasks");
        reference.child(mIntent.getStringExtra("taskId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTask = dataSnapshot.getValue(Task.class);

                tvTime.setText(mTask.getTimestamp());
                edTitle.setText(mTask.getTitle());
                edDes.setText(mTask.getDes());

                DatabaseReference referencePro = firebaseDatabase.getReference("Projects");
                referencePro.child(mTask.getProjectId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProject = dataSnapshot.getValue(Project.class);
                        String usId = mProject.getUsId();
                        listMemId = stringToList(usId);
                        listMemId.add(mProject.getUid());


                        actionBar.setTitle(mProject.getTitle());
                        DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
                        for (final String memId : listMemId){
                            referenceUser.child(memId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User tmpUser = dataSnapshot.getValue(User.class);
//                            Toast.makeText(getBaseContext(), tmpUser.getName(), Toast.LENGTH_LONG).show();
                                    listMem.add(tmpUser.getName());
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

        btnGive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveTask();
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

    private void giveTask(){
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
        if(usIdSelect.equals("")){
            Toast.makeText(getApplicationContext(), "Chưa chọn người để giao việc", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!title.equals("") && !des.equals("")&&!usIdSelect.equals("")) {
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("usId", usIdSelect);
            hashMap.put("title", title);
            hashMap.put("des", des);
            hashMap.put("status", 1);

            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference referenceUser = firebaseDatabase.getReference("Users");
            referenceUser.child(usIdSelect).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameT = dataSnapshot.getValue(User.class).getName();
                    hashMap.put("leadName", nameT);


                    DatabaseReference reference = firebaseDatabase.getReference("Tasks");
                    reference.child(mTask.getId()).updateChildren(hashMap);

                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




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
//        Intent intentT = new Intent(GiveTaskActivity.this, ProjectActivity.class);
//        startActivity(intentT);
        finish();
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
