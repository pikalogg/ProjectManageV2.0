package com.pikalong.projectmanagev11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pikalong.projectmanagev11.adapter.ProjectAdapter;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.pikalong.projectmanagev11.R.string.open;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActionBar actionBar;
    TextView btn_dang, btn_da;
    ListView listView;
    LinearLayout l_tmp;
    ImageButton btn_add;

    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    List<Project> projects_dang;
    List<Project> projects_da;
    ProjectAdapter projectAdapter_dang;
    ProjectAdapter projectAdapter_da;

    boolean inDang = true;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addControl();
        addEvent();
        addData();
    }

    private void addControl(){


        actionBar = getSupportActionBar();
        actionBar.setTitle("Pika Team");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_option_n);

        navigationView = findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout , R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();


        btn_da = findViewById(R.id.btn_da);
        btn_dang = findViewById(R.id.btn_dang);
        listView = findViewById(R.id.listview);
        l_tmp = findViewById(R.id.l_tmp);

        btn_add = findViewById(R.id.btn_add);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null){
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void addEvent(){

        btn_da.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                listView.setAdapter(projectAdapter_da);
                l_tmp.setGravity(Gravity.RIGHT);
                inDang = false;
            }
        });
        btn_dang.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                listView.setAdapter(projectAdapter_dang);
                l_tmp.setGravity(Gravity.LEFT);
                inDang = true;
                Log.d(WALLPAPER_SERVICE, "onClick: da hoan thanh");
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AddProjectActivity.class);
                startActivity(intent);
//                finish();
            }
        });

    }

    private  void addData(){
//        projects_dang = new ArrayList<>();
//        projects_dang.add(new Project("Dự án 1", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 2", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 3", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 4", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 5", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 6", "chả cần làm gì"));
//        projects_dang.add(new Project("Dự án 7", "chả cần làm gì"));
//
//        projects_da = new ArrayList<>();
//        projects_da.add(new Project("Dự án xong 1", "chả cần làm gì"));
//        projects_da.add(new Project("Dự án xong 2", "chả cần làm gì"));
//
//        projectAdapter_dang = new ProjectAdapter(projects_dang , getApplicationContext());
//        projectAdapter_da = new ProjectAdapter(projects_da , getApplicationContext());
//        listView.setAdapter(projectAdapter_dang);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(DashboardActivity.this, ProjectActivity.class);
//                if(inDang){
//                    intent.putExtra("namePro", projects_dang.get(i).getTitle());
//                } else {
//                    intent.putExtra("namePro", projects_da.get(i).getTitle());
//                }
//                startActivity(intent);
////                finish();
//            }
//        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(drawerToggle.onOptionsItemSelected(item))
                    return true;
            case R.id.action_file:

                return  true;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        switch (id){
            case R.id.item_pi1:
                firebaseAuth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
        return false;
    }
}
