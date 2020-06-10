package com.pikalong.projectmanagev11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pikalong.projectmanagev11.R;

import java.util.List;

public class FileAdapte extends BaseAdapter {
    Context context;
    List<String> listFiles;

    public FileAdapte(Context context, List<String> listFiles) {
        this.context = context;
        this.listFiles = listFiles;
    }

    @Override
    public int getCount() {
        return listFiles.size();
    }

    @Override
    public Object getItem(int i) {
        return listFiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileViewHolder fileViewHolder = null;
        String file = listFiles.get(i);
        if(file.length() > 13) file = file.substring(13);
        if(view == null){
            fileViewHolder = new FileViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_filename, null);

            fileViewHolder.tvFile = view.findViewById(R.id.tvFile);
            fileViewHolder.tvFile.setText(file);

            view.setTag(fileViewHolder);

        } else {
            fileViewHolder = (FileViewHolder) view.getTag();

            fileViewHolder.tvFile.setText(file);

        }
        return view;
    }

    class FileViewHolder{
        TextView tvFile;
    }
}
