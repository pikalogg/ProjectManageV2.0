package com.pikalong.projectmanagev11;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class SuccessfulTaskActivity extends AppCompatActivity {
    ActionBar actionBar;
    Intent intent;

    TextView tv_nameSu, tv_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_task);
        addControl();
        addEvent();
    }
    private void addControl(){
        intent = getIntent();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(intent.getStringExtra("title"));

        tv_nameSu = findViewById(R.id.tv_nameSu);
        tv_nameSu.setText(intent.getStringExtra("name"));
        tv_des = findViewById(R.id.tv_des);
        tv_des.setText(intent.getStringExtra("des"));
    }
    private void addEvent(){

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
