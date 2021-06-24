package com.kptech.peps.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.model.Chat;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.Message;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public  static final int MSG_TYPE_LEFT = 0;
    public  static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Message> mMessages;

    public MessageAdapter(Context mContext, List<Message> mMessages){
        this.mMessages = mMessages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        if (message.getType().equals("photo")) {
            holder.show_message.setVisibility(View.GONE);
            holder.message_pic.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(message.getContent()).centerCrop().fit().error(R.drawable.ic_pic_error).into(holder.message_pic);
        } else {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.message_pic.setVisibility(View.GONE);
            holder.show_message.setText(message.getContent());
        }
        if (position == mMessages.size()-1) {
            if (message.getUser_pic() != null && !message.getUser_pic().equals("")) {
                holder.profile_image.setAlpha(1.0f);
                Picasso.with(mContext).load(message.getUser_pic()).centerCrop().fit().error(R.drawable.ic_user_pic).into(holder.profile_image);
            } else {
                holder.profile_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_pic));
            }
            holder.send_time.setVisibility(View.VISIBLE);
            holder.send_time.setText(Utils.howLongAgo(message.getDate()));
        } else {
            holder.profile_image.setAlpha(0.0f);
            holder.send_time.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView show_message;
        ImageView profile_image, message_pic;
        TextView send_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.iv_avatar);
            send_time = itemView.findViewById(R.id.send_time);
            message_pic = itemView.findViewById(R.id.message_image);
        }
    }

    @Override
    public int getItemViewType(int position){
        UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
        if (mMessages.get(position).getSender_id().equals(Utils.getKey(userAccount.getEmail()))){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
