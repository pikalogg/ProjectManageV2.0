package com.pikalong.projectmanagev11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikalong.projectmanagev11.R;
import com.pikalong.projectmanagev11.model.Project;
import com.pikalong.projectmanagev11.model.Task;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    List<Task> tasks;
    Context context;

    public TaskAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int i) {
        return tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class TaskViewHolder{
        TextView name, time, title, des;
        ImageView img;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TaskViewHolder taskViewHolder = null;
        if (view == null){
            taskViewHolder = new TaskAdapter.TaskViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_row_task, null);
            taskViewHolder.name = view.findViewById(R.id.tv_name);
            taskViewHolder.time = view.findViewById(R.id.tv_time);
            taskViewHolder.title = view.findViewById(R.id.tv_title);
            taskViewHolder.des = view.findViewById(R.id.tv_des);
            taskViewHolder.img = view.findViewById(R.id.img_task);

            Task task = tasks.get(i);
            taskViewHolder.name.setText(task.getUid());
            taskViewHolder.time.setText(task.getTimestamp());
            taskViewHolder.title.setText(task.getTitle());
            taskViewHolder.des.setText(task.getDes());
            Picasso.get().load(task.getImage())
                    .error(R.drawable.df_av_task)
                    .placeholder(R.drawable.df_av_task)
                    .into(taskViewHolder.img);
            view.setTag(taskViewHolder);
        }
        else {
            Task task = tasks.get(i);
            taskViewHolder.name.setText(task.getUid());
            taskViewHolder.time.setText(task.getTimestamp());
            taskViewHolder.title.setText(task.getTitle());
            taskViewHolder.des.setText(task.getDes());
            Picasso.get().load(task.getImage())
                    .error(R.drawable.df_av_task)
                    .placeholder(R.drawable.df_av_task)
                    .into(taskViewHolder.img);
        }
        return view;
    }
}
