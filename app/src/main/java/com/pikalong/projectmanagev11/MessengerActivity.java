package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.adapter.MessengerAdapter;
import com.pikalong.projectmanagev11.model.ChatMessage;
import com.pikalong.projectmanagev11.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {
    ActionBar actionBar;

    RecyclerView listOfMessages;
    List<ChatMessage> chatMessages;
    MessengerAdapter adapter;

    FloatingActionButton fab;

    EditText input;

    String mUserName = "Name";
    String projectId;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Thảo luận");

        projectId = getIntent().getStringExtra("projectId");

        input = findViewById(R.id.input);
        fab =  findViewById(R.id.fab);
        listOfMessages = findViewById(R.id.list_of_messages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        listOfMessages.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();
        chatMessages = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Messengers");

        adapter = new MessengerAdapter(MessengerActivity.this, chatMessages);
        listOfMessages.setAdapter(adapter);

        database.getReference("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.getValue(User.class).getName();
//                readMess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database.getReference("Projects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readMess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



//        readMess();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = input.getText().toString();
                if(!mess.equals("")){
                    sendMess(mess);
                }
                input.setText("");
            }
        });


    }


    private void sendMess(String mess){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageText", mess);
        hashMap.put("messageUser", mUserName);
        hashMap.put("messageTime", new Timestamp(System.currentTimeMillis()).toString().substring(0,19));
        hashMap.put("projectId", projectId);

        reference.push().setValue(hashMap);

    }

    private void readMess(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
//                    Toast.makeText(getBaseContext(), chatMessage.getMessageText(), Toast.LENGTH_LONG).show();
                    if(chatMessage.getProjectId().equals(projectId)){
                        chatMessages.add(chatMessage);
                        adapter.notifyDataSetChanged();
                    }

                }
//                Toast.makeText(getBaseContext(), chatMessages.size(), Toast.LENGTH_LONG).show();

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
}
