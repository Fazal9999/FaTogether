package com.kptech.peps.recycler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kptech.peps.R;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.model.Conversation;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentMessageAdapter extends RecyclerView.Adapter<RecentMessageAdapter.MyMessageViewHolder> implements View.OnCreateContextMenuListener{

    private Context mContext;
    private List<Conversation> convoData = new ArrayList<>();

    public void setConvoList(List<Conversation> convos) {
        if (convos != null) {
            convoData = convos;
        } else {
            convoData = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public RecentMessageAdapter.MyMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_message, parent, false);
        return new RecentMessageAdapter.MyMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentMessageAdapter.MyMessageViewHolder holder, int position) {
        Conversation convo = convoData.get(position);

        if (convo != null) {
            if (convo.getLatestMessage().getUser_pic() != null && !convo.getLatestMessage().getUser_pic().equals("")) {
                Picasso.with(mContext).load(convo.getLatestMessage().getUser_pic()).centerCrop().fit().error(R.drawable.ic_user_pic).into(holder.msgPic);
            } else {
                holder.msgPic.setImageResource(R.drawable.ic_user_pic);
            }
            holder.msgTime.setText(Utils.howLongAgo(convo.getLatestMessage().getDate()));
            holder.msgPreview.setText(convo.getLatestMessage().getMessage());
            holder.msgUsername.setText(convo.getName());

            if (!convo.getLatestMessage().getIs_read()) {
                holder.messageLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorHighlight));
            } else {
                holder.messageLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
            }

            holder.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!convo.getLatestMessage().getIs_read()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(FirebaseConstants.USERS)
                                .child(Utils.getKey(DataHolder.getInstance().getmCurrentUser().getEmail()))
                                .child("conversations");
                        convoData.remove(position);
                        convo.getLatestMessage().setIs_read(true);
                        convoData.add(0, convo);
                        myRef.setValue(convoData);
                    }

                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("other_user_key", convo.getOther_user_id());
                    intent.putExtra("convo_id", convo.getId());
                    intent.putExtra("other_user_name", convo.getName());

                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (convoData != null) {
            return convoData.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout messageLayout;
        public CircleImageView msgPic;
        public TextView msgUsername, msgPreview, msgTime;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageLayout = itemView.findViewById(R.id.ll_message);
            msgPic = itemView.findViewById(R.id.msg_avatar);
            msgUsername = itemView.findViewById(R.id.msg_username);
            msgPreview = itemView.findViewById(R.id.msg_preview);
            msgTime = itemView.findViewById(R.id.msg_timestamp);
        }
    }
}
