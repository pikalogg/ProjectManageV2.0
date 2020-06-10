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
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProjectAdapter extends BaseAdapter {

    List<Project> projects;
    Context context;

    public ProjectAdapter(List<Project> projects, Context context) {
        this.projects = projects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Object getItem(int i) {
        return projects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    class ProjectViewHolder{
        TextView title, des, time;
        ImageView image;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProjectViewHolder projectViewHolder = null;
        if (view == null){
            projectViewHolder = new ProjectViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_row_project, null);

            projectViewHolder.title = view.findViewById(R.id.t_title);
            projectViewHolder.des = view.findViewById(R.id.t_des);
            projectViewHolder.time = view.findViewById(R.id.t_time);
            projectViewHolder.image = view.findViewById(R.id.img_pro);

            Project project = projects.get(i);
            projectViewHolder.title.setText(project.getTitle());
            projectViewHolder.des.setText(project.getDes());
            projectViewHolder.time.setText(project.getTimestamp());
            Picasso.get().load(project.getImage())
                    .error(R.drawable.haha)
                    .placeholder(R.drawable.haha)
                    .into(projectViewHolder.image);
            view.setTag(projectViewHolder);
        }
        else {
            projectViewHolder = (ProjectViewHolder) view.getTag();
            Project project = projects.get(i);
            projectViewHolder.title.setText(project.getTitle());
            projectViewHolder.des.setText(project.getDes());
            projectViewHolder.time.setText(project.getTimestamp());
            Picasso.get().load(project.getImage())
                    .error(R.drawable.haha)
                    .placeholder(R.drawable.haha)
                    .into(projectViewHolder.image);
        }
        return view;
    }

}
