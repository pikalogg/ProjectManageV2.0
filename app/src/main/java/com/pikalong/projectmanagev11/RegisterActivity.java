package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.model.User;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    ActionBar actionBar;

    EditText nameEd, emailEd, passwordEd, phoneEd;
    Button btn_register;
    SweetAlertDialog sweetAlertDialog;
    TextView have_account;
    String name = "" , phone;
    Boolean isInvalid = true;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControl();
        addEvent();
    }
    private void addControl(){
        actionBar = getSupportActionBar();
        actionBar.hide();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("Đăng ký");


        nameEd = findViewById(R.id.nameEd);
        emailEd = findViewById(R.id.emailEd);
        passwordEd = findViewById(R.id.passwordEd);
        phoneEd = findViewById(R.id.phoneEd);
        btn_register = findViewById(R.id.btn_register);
        have_account = findViewById(R.id.have_account);
        firebaseAuth = FirebaseAuth.getInstance();
        sweetAlertDialog = new SweetAlertDialog(this);
        sweetAlertDialog.setTitleText("Đang đăng ký...");
        sweetAlertDialog.setContentText("Hãy chờ");
        sweetAlertDialog.setCanceledOnTouchOutside(false);

    }
    private void addEvent(){
        emailEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final String email = emailEd.getText().toString();
                if(!hasFocus)
                    checkEmail(v, email.trim());
            }
        });
        have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //xử lý nút register
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInvalid = true;
                String email = emailEd.getText().toString();
                String pass = passwordEd.getText().toString();
                name = nameEd.getText().toString();
                phone = phoneEd.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailEd.setError("Chưa đúng định dạng Email");
                    emailEd.setFocusable(true);
                    isInvalid = false;
                }
                if(pass.length() < 6)
                {
                    passwordEd.setError("Mật khẩu không ngắn hơn 6 ký tự");
                    passwordEd.setFocusable(true);
                    isInvalid = false;
                }
                if(TextUtils.isEmpty(name))
                {
                    nameEd.setError("Không phải tên người");
                    nameEd.setFocusable(true);
                    isInvalid = false;
                }
                if(isInvalid)
                {
                    registerUser(email, pass);
                }

            }
        });
    }
    //////////////////////////////////////////////////////

    private void registerUser(String email, String pass) {
        sweetAlertDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            String email = firebaseUser.getEmail();
                            String uid = firebaseUser.getUid();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", name);
                            hashMap.put("typingTo", "noOne");
                            hashMap.put("projects", "");
                            hashMap.put("phone", phone);
                            hashMap.put("birthday", ""); //lấy sau
                            hashMap.put("gender", "Nam");//lấy sau
                            hashMap.put("image", ""); //lấy sau
                            hashMap.put("cover", "");//lấy sau

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference reference = firebaseDatabase.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            sweetAlertDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            sweetAlertDialog.dismiss();
                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Lỗi!")
                                    .setContentText("Đăng ký thất bại")
                                    .show();
                        }
                }
            });
    }




    private void checkEmail(View v, final String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    emailEd.setError("Email đã được đăng ký trước đó");
                    emailEd.setFocusable(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    if(user.getEmail().equals(email))
                    {
                        emailEd.setError("Email đã được đăng ký trước đó");
                        emailEd.setFocusable(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    ////////////////////////////////////////////////////
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
}
