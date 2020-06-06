package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.adapter.MemberBoxAdapter;
import com.pikalong.projectmanagev11.adapter.ProjectAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddProjectActivity extends AppCompatActivity {
    ActionBar actionBar;

    ListView lvAddMem;
    LinearLayout llAddMem;
    List<String> members;
    List<String> memberUid;
    MemberBoxAdapter memberBoxAdapter;

    boolean checkMem = false;
    String tmpUid;
    TextView tvAddMem;

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
                                    User user = dataSnapshot.getValue(User.class);
                                    checkMem = true;
                                    tvMemName.setText(user.getName());
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

    }

    private  void addData(){
        memberBoxAdapter = new MemberBoxAdapter(members, getApplicationContext());

        lvAddMem.setAdapter(memberBoxAdapter);

        lvAddMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                members.remove(i);
                memberUid.remove(i);
                memberBoxAdapter = new MemberBoxAdapter(members, getApplicationContext());
                lvAddMem.setAdapter(memberBoxAdapter);
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
//        super.onBackPressed();
        finish();
    }
}
