package com.pikalong.projectmanagev11;

        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.Switch;
        import android.widget.TextView;

        import com.pikalong.projectmanagev11.adapter.ProjectAdapter;
        import com.pikalong.projectmanagev11.adapter.TaskAdapter;
        import com.pikalong.projectmanagev11.model.Project;
        import com.pikalong.projectmanagev11.model.Task;

        import java.util.ArrayList;
        import java.util.List;

public class ProjectActivity extends AppCompatActivity {
    ActionBar actionBar;
    Intent intent;

    int in = 0;
    ListView listView;
    TextView btn_gv, btn_dl, btn_kt, btn_ht;
    LinearLayout l_tmp, l_tmp2;

    List<Task> tasks;
    List<Task> tasks_gv;
    List<Task> tasks_dl;
    List<Task> tasks_kt;
    List<Task> tasks_ht;
    TaskAdapter taskAdapter_gv;
    TaskAdapter taskAdapter_dl;
    TaskAdapter taskAdapter_kt;
    TaskAdapter taskAdapter_ht;

    ImageButton btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        addControl();
        addEvent();
        addData();
    }

    private void addControl(){
        intent = getIntent();
        String title = intent.getStringExtra("namePro");
        actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.lv_conten);
        btn_gv = findViewById(R.id.btn_giaoviec);
        btn_dl = findViewById(R.id.btn_danglam);
        btn_kt = findViewById(R.id.btn_kiemtra);
        btn_ht = findViewById(R.id.btn_hoanthanh);

        l_tmp = findViewById(R.id.l_tmp);
        l_tmp2 = findViewById(R.id.l_tmp2);

        btn_add = findViewById(R.id.btn_add);

    }
    private void addEvent(){
        btn_gv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_gv);
                l_tmp.setGravity(Gravity.LEFT);
                l_tmp2.setGravity(Gravity.LEFT);
                in = 0;
            }
        });
        btn_dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_dl);
                l_tmp.setGravity(Gravity.LEFT);
                l_tmp2.setGravity(Gravity.RIGHT);
                in = 1;
            }
        });
        btn_kt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_kt);
                l_tmp.setGravity(Gravity.RIGHT);
                l_tmp2.setGravity(Gravity.LEFT);
                in = 2;
            }
        });
        btn_ht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(taskAdapter_ht);
                l_tmp.setGravity(Gravity.RIGHT);
                l_tmp2.setGravity(Gravity.RIGHT);
                in = 3;
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectActivity.this, AddTaskActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }

    private  void addData(){
//        tasks = new ArrayList<>();
//        //gv
//        tasks.add(new Task("công việc cao cả", "đứng im một chỗ"));
//        tasks.add(new Task("công việc bần hàn", "đi qua đi lại"));
//        tasks.add(new Task());
//        tasks.add(new Task());
//        //dl
//        tasks.add(new Task("Kiểm tra chính tả", "Bạn sửa lỗi, nhưng bạn đã mất mạch ý tưởng. Để tránh sự ngắt quãng đó, bạn có thể tắt kiểm tra chính tả, rồi kiểm tra chính tả bằng cách thủ công khi bạn đã viết xong. Đây là cách thực hiện.", 1));
//        tasks.add(new Task("Làm slide thuyết trính", "Ngay trong PowerPoint có rất nhiều mẫu theme, hình nền PowerPoint để chúng ta lựa chọn. Mỗi một hình nền đều theo nhiều chủ đề khác nhau, bạn nên lựa chọn hình nền phù hợp với nội dung cũng như đối tượng trình chiếu.", 1));
//        //kt
//        Task tmp1 = new Task("Mẫu để kiểm tra", "không làm gì", 2);
//        tmp1.setNameSu("Vũ Thị Ngọc Lê");
//        tasks.add(tmp1);
//
//        //ht
//        Task tmp2 = new Task("Mẫu để kiểm tra hoàn thành", "không làm gì", 3);
//        tmp2.setNameSu("Vũ Thị Ngọc Lê");
//        tasks.add(tmp2);
//
//        tasks_gv = new ArrayList<>();
//        tasks_dl = new ArrayList<>();
//        tasks_kt = new ArrayList<>();
//        tasks_ht = new ArrayList<>();
//
//        for (int i=0;i<tasks.size();i++){
//            Task task = tasks.get(i);
//            switch (task.getStatus()){
//                case 0:
//                    tasks_gv.add(task);
//                    break;
//                case 1:
//                    tasks_dl.add(task);
//                    break;
//                case 2:
//                    tasks_kt.add(task);
//                    break;
//                case 3:
//                    tasks_ht.add(task);
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        taskAdapter_gv = new TaskAdapter(tasks_gv, getApplicationContext());
//        taskAdapter_dl = new TaskAdapter(tasks_dl, getApplicationContext());
//        taskAdapter_kt = new TaskAdapter(tasks_kt, getApplicationContext());
//        taskAdapter_ht = new TaskAdapter(tasks_ht, getApplicationContext());
//
//        listView.setAdapter(taskAdapter_gv);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent myIntent = null;
//                switch (in){
//                    case 0:
//                        myIntent = new Intent(ProjectActivity.this, GiveTaskActivity.class);
//                        myIntent.putExtra("name", tasks_gv.get(i).getName());
//                        myIntent.putExtra("title", tasks_gv.get(i).getTitle());
//                        myIntent.putExtra("des", tasks_gv.get(i).getDes());
//                        break;
//                    case 1:
//                        myIntent = new Intent(ProjectActivity.this, DeployingTaskActivity.class);
//                        myIntent.putExtra("name", tasks_dl.get(i).getName());
//                        myIntent.putExtra("title", tasks_dl.get(i).getTitle());
//                        myIntent.putExtra("des", tasks_dl.get(i).getDes());
//                        break;
//                    case 2:
//                        myIntent = new Intent(ProjectActivity.this, InspectTaskActivity.class);
//                        myIntent.putExtra("name", tasks_kt.get(i).getNameSu());
//                        myIntent.putExtra("title", tasks_kt.get(i).getTitle());
//                        myIntent.putExtra("des", tasks_kt.get(i).getDes());
//                        break;
//                    case 3:
//                        myIntent = new Intent(ProjectActivity.this, SuccessfulTaskActivity.class);
//                        myIntent.putExtra("name", tasks_ht.get(i).getNameSu());
//                        myIntent.putExtra("title", tasks_ht.get(i).getTitle());
//                        myIntent.putExtra("des", tasks_ht.get(i).getDes());
//                        break;
//                    default:
//                        break;
//                }
//                myIntent.putExtra("namePro", intent.getStringExtra("namePro"));
//                startActivity(myIntent);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pro, menu);
        return true;
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
            case R.id.action_file:
                btn_gv.setText("pika");
                return true;
            case R.id.action_delete:

                return true;
            case R.id.action_return:
//
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
