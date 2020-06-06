package com.pikalong.projectmanagev11.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikalong.projectmanagev11.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemberBoxAdapter extends BaseAdapter {
    List<String> members;
    Context context;

    public MemberBoxAdapter(List<String> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int i) {
        return members.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    class MemberViewHolder{
        TextView memberName;
        ImageView imgX;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MemberViewHolder memberViewHolder = null;
        if (view == null){
            memberViewHolder = new MemberBoxAdapter.MemberViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.member_box, null);
            memberViewHolder.memberName = view.findViewById(R.id.tvMemName);
            memberViewHolder.imgX = view.findViewById(R.id.imgX);

            String memberName = members.get(i);
            memberViewHolder.memberName.setText(memberName);
            memberViewHolder.imgX.setImageResource(R.drawable.ic_clear_black_24dp);

            view.setTag(memberViewHolder);
        }
        else {
            memberViewHolder = (MemberViewHolder) view.getTag();

            String memberName = members.get(i);
            memberViewHolder.memberName.setText(memberName);
            memberViewHolder.imgX.setImageResource(R.drawable.ic_clear_black_24dp);
        }
        return view;
    }
}
