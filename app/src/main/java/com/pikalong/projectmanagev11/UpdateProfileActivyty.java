package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pikalong.projectmanagev11.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateProfileActivyty extends AppCompatActivity {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private static final int PICK_PHOTO_FOR_COVER = 2;
    ActionBar actionBar;
    /////////////
    ImageView imgAva, imgCover, btnChangeAva, btnChangeCover;
    ImageButton btnChangeGender;
    Button btnChangePass;
    EditText edName, edBirthday, edPhone ;
    TextView tvName, tvGender, tvEmail;
    //////////////
    Uri imgUriAva, imgUriCov;

    User mUser;
    SweetAlertDialog sweetAlertDialog;


    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_activyty);

        addControl();
        addEvent();
        addData();
    }



    private void addControl() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Chỉnh sửa thông tin");
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgAva = findViewById(R.id.imgAva);
        imgCover = findViewById(R.id.imgCover);
        btnChangeAva = findViewById(R.id.btnChangeAva);
        btnChangeCover = findViewById(R.id.btnChangeCover);

        btnChangeGender = findViewById(R.id.btnChangeGender);

        btnChangePass = findViewById(R.id.btnChangePass);

        edName = findViewById(R.id.edName);
        edBirthday = findViewById(R.id.edBirthday);
        edPhone = findViewById(R.id.edPhone);

        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvGender = findViewById(R.id.tvGender);


        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCanceledOnTouchOutside(false);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }


    private void addEvent() {
        btnChangeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myGender = tvGender.getText().toString();
                if (myGender.equals("Nam")){
                    tvGender.setText("Nữ");
                }
                if (myGender.equals("Nữ")){
                    tvGender.setText("Khác");
                }
                if (myGender.equals("Khác")){
                    tvGender.setText("Nam");
                }
            }

        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(UpdateProfileActivyty.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);

                final EditText edOldPass = mView.findViewById(R.id.edOldPass);
                final EditText edNewPass = mView.findViewById(R.id.edNewPass);
                final EditText edReNewPass = mView.findViewById(R.id.edReNewPass);
                Button btnOk = mView.findViewById(R.id.btnOk);
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

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(edNewPass.getText().toString().length() < 6){
                            edNewPass.setError("Mật khẩu quá ngắn");
                            edNewPass.setFocusable(true);
                        }
                        else if(edNewPass.getText().toString().equals(edReNewPass.getText().toString())){
                            updatePass(edOldPass.getText().toString(), edNewPass.getText().toString());
                        }
                        else {
                            edReNewPass.setError("Không trùng khớp");
                        }
                    }
                });


                alertDialog.show();
            }
        });

        btnChangeAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageAvata();
            }
        });
        btnChangeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageCover();
            }
        });
    }

    private void addData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                tvName.setText(mUser.getName());
                tvEmail.setText(mUser.getEmail());
                edName.setText(mUser.getName());
                if(!mUser.getBirthday().equals("")){
                    edBirthday.setText(mUser.getBirthday());
                }
                tvGender.setText(mUser.getGender());
                edPhone.setText(mUser.getPhone());

                if(mUser.getImage().equals("")){
                    Picasso.get().load(R.drawable.default_avatar).into(imgAva);
                }
                else {
                    Picasso.get().load(mUser.getImage()).into(imgAva);
                }

                if(mUser.getCover().equals("")){
                    Picasso.get().load(R.drawable.defause_bgr).into(imgCover);
                }
                else {
                    Picasso.get().load(mUser.getCover()).into(imgCover);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemSave:
                // luu thong tin lai
                try {
                    SaveProfile();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }



    ////////////// choie img
    public void pickImageAvata() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }
    public void pickImageCover() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_COVER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error

                return;
            }
            imgUriAva = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUriAva);
                imgAva.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == PICK_PHOTO_FOR_COVER && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
            imgUriCov = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUriCov);
                imgCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    //////////////////////////
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void SaveProfile() throws InterruptedException {

        if( edBirthday.getText().toString().equals("")){
            edBirthday.setError("Vui lòng điền ngày sinh");
            edBirthday.setFocusable(true);
            return;
        }
        if (!edBirthday.getText().toString().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")){
            edBirthday.setError("Sai định dạng");
            edBirthday.setFocusable(true);
            return;
        }
        ///up text
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", edName.getText().toString());
        hashMap.put("phone", edPhone.getText().toString());
        hashMap.put("birthday", edBirthday.getText().toString());
        hashMap.put("gender", tvGender.getText().toString());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users");
        reference.child(mUser.getUid()).updateChildren(hashMap);

        // up img
        upLoadImage(imgUriAva, 1);
        imgUriAva = null;
        upLoadImage(imgUriCov, 2);
        imgUriCov = null;

        new SweetAlertDialog(UpdateProfileActivyty.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Thay đổi đã được lưu")
                .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private void upLoadImage(final Uri uri, final int stt){
        if(uri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            uploadTask = (UploadTask) fileReference.putFile(uri)
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
                                HashMap<String, Object> hashMap = new HashMap<>();
                                if(stt == 1){
                                    hashMap.put("image", downloadUrl.toString());
                                } else if (stt == 2){
                                    hashMap.put("cover", downloadUrl.toString());
                                }

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference reference = firebaseDatabase.getReference("Users");
                                reference.child(mUser.getUid()).updateChildren(hashMap);

                                //////////////
//                                Toast.makeText(getBaseContext(),getFileName(uri), Toast.LENGTH_LONG).show();

                                // reset muser
                                reference.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        mUser = dataSnapshot.getValue(User.class);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                });
        }
    }

    private void updatePass(String currentpass, final String newpass) {
        sweetAlertDialog.setTitleText("Updating password");
        sweetAlertDialog.setContentText("Please wait.....");
        sweetAlertDialog.show();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,currentpass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    sweetAlertDialog.dismiss();
                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                new SweetAlertDialog(UpdateProfileActivyty.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Mật khẩu đã được thay đổi")
                                        .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                            else
                            {
                                new SweetAlertDialog(UpdateProfileActivyty.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Thay đổi mật khẩu thất bại")
                                        .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }
                else
                {
                    sweetAlertDialog.dismiss();
                    new SweetAlertDialog(UpdateProfileActivyty.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Mật khẩu cũ không khớp")
                            .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            }
        });
    }

}
