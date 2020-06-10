package com.pikalong.projectmanagev11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikalong.projectmanagev11.R;
import com.pikalong.projectmanagev11.model.ChatMessage;

import java.util.List;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.MessengerViewHolder> {

    Context context;
    List<ChatMessage> chatMessages;

    public MessengerAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public MessengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mess,parent,false);
        return new MessengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessengerViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.messageText.setText(chatMessage.getMessageText());
        holder.messageUser.setText(chatMessage.getMessageUser());
        holder.messageTime.setText(chatMessage.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class MessengerViewHolder extends RecyclerView.ViewHolder{
        TextView messageText;
        TextView messageUser;
        TextView messageTime;

        public MessengerViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text);
            messageUser = itemView.findViewById(R.id.message_user);
            messageTime = itemView.findViewById(R.id.message_time);
        }
    }
}
