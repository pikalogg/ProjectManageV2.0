package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
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
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.model.User;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button btn_login;
    SweetAlertDialog sweetAlertDialog;


    private static final int RC_SIGN_IN = 100;
    EditText emailEd, passwordEd;
    TextView not_have_account, recover_pass;
    SignInButton btn_googlelogin;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControl();
        addEvent();
    }
    private void addControl(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Đăng nhập");

        btn_login = findViewById(R.id.btn_login);
        not_have_account = findViewById(R.id.not_have_account);


//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(LoginActivity.this.getResources().getString(R.string.default_web_client_id))
//                .requestEmail().build();

//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailEd = findViewById(R.id.emailEd);
        passwordEd = findViewById(R.id.passwordEd);
        btn_login = findViewById(R.id.btn_login);
        not_have_account = findViewById(R.id.not_have_account);
        recover_pass = findViewById(R.id.recover_pass);
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCanceledOnTouchOutside(false);

        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String emailLast = sharedPreferences.getString("EMAIL_LAST", "None");
        if (!emailLast.equals("None")) {
            emailEd.setText(emailLast);
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity.this.getResources().getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btn_googlelogin = findViewById(R.id.btn_googlelogin);


        firebaseAuth = FirebaseAuth.getInstance();

    }
    private void addEvent(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEd.getText().toString();
                String pass = passwordEd.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(email)) {
                    emailEd.setError("Email không được để trống");
                    emailEd.setFocusable(true);
                } else if (TextUtils.isEmpty(pass)) {
                    passwordEd.setError("Mật khẩu không được để trống");
                    passwordEd.setFocusable(true);
                } else {
                    loginUser(email, pass);
                }
            }
        });
        ////////////////////////////////////////////////////////////

        emailEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final String email = emailEd.getText().toString();
                if (!hasFocus) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEd.setError("Email không được để trống");
                        emailEd.setFocusable(true);
                    } else {
                        checkEmail(v, email.trim());
                    }
                }
            }
        });

        not_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        recover_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverDialog();

            }
        });

        btn_googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.setTitleText("Đăng nhập bằng Google....");
                sweetAlertDialog.show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }


    private void loginUser(String email, String pass){
        sweetAlertDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sweetAlertDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            sweetAlertDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Lỗi")
                                    .setContentText("Tài khoản hoặc mật khẩu không chính xác")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    private void showRecoverDialog() {
        new LovelyTextInputDialog(this)
                .setTopColor(Color.parseColor("#40C4FF"))
                .setTitle("Lấy lại mật khẩu")
                .setMessage("Vui lòng nhập email của bạn")
                .setInputFilter("Email không hợp lệ", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return !TextUtils.isEmpty(text.trim()) || Patterns.EMAIL_ADDRESS.matcher(text.trim()).matches();
                    }
                })
                .setConfirmButton("Gửi", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        sendRecover(text.trim());
                    }
                }).show();
    }
    private void sendRecover(String email) {
        sweetAlertDialog.setTitleText("Đang gửi\nHãy chờ...");
        sweetAlertDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sweetAlertDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thành công!")
                                    .setContentText("Hãy kiểm tra Email của bạn !")
                                    .show();
                            //Toast.makeText(LoginActivity.this, "Please check your email !", Toast.LENGTH_LONG).show();
                        } else {
                            sweetAlertDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Lỗi!")
                                    .setContentText("Lỗi! Vui lòng thử lại sau")
                                    .show();

                            //Toast.makeText(LoginActivity.this,"Cannot failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    //////////////////////////////////////////////////
    private void checkEmail(View v, final String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isRegisted = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getEmail().equals(email)) {
                        isRegisted = true;
                    }
                }
                if (!isRegisted) {
                    emailEd.setError("Email chưa được đăng ký");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
