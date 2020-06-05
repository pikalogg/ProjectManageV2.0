package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.pikalong.projectmanagev11.model.User;

import java.io.IOException;

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

                EditText edOldPass = mView.findViewById(R.id.edOldPass);
                EditText edNewPass = mView.findViewById(R.id.edNewPass);
                EditText edReNewPass = mView.findViewById(R.id.edReNewPass);
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                edName.setText(user.getName());
                if(!user.getBirthday().equals("")){
                    edBirthday.setText(user.getBirthday());
                }
                tvGender.setText(user.getGender());
                edPhone.setText(user.getPhone());

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
            Uri imgData = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgData);
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
            Uri imgData = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgData);
                imgCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
