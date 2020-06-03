package com.pikalong.projectmanagev11;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class AddProjectActivity extends AppCompatActivity {
    ActionBar actionBar;

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
    }
    private void addEvent(){

    }

    private  void addData(){

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
