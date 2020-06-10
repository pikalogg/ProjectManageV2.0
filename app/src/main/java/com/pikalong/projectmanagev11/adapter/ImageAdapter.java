package com.pikalong.projectmanagev11.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pikalong.projectmanagev11.InspectTaskActivity;
import com.pikalong.projectmanagev11.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> listImage;
    private Activity activity;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    StorageReference reference;

    public ImageAdapter(Activity activity, List<String> listImage) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Uploads");
        this.listImage = listImage;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        final String imgUrl = listImage.get(position);
        reference = storageReference.child(imgUrl);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();

                Picasso.get().load(url)
                        .error(R.drawable.not_found)
                        .placeholder(R.drawable.not_found)
                        .into(holder.image);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, imgUrl, Toast.LENGTH_LONG).show();
                dowload(imgUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }


    /////////////////////////////////////
    private void dowload(final String child){
        reference = storageReference.child(child);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                dowloadFile(activity.getApplicationContext(), child, Environment.DIRECTORY_DOWNLOADS, url);
            }
        });

    }

    private void dowloadFile(Context context, String nameFile, String destinationDirectoyr , String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectoyr, nameFile);

        downloadManager.enqueue(request);

    }
}
