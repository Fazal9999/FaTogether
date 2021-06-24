package com.kptech.peps.recycler;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.model.CommentsItems;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class ChatCommentListAdapter extends RecyclerView.Adapter<ChatCommentListAdapter.MyViewHolder> {
    private List<CommentsItems> itemdata = new ArrayList<>();
    private Context mContext;
    private boolean mShowAllComments;


    public ChatCommentListAdapter() {

    }

    public void setItemList(CommentsItems data) {
        itemdata.add(data);
        notifyItemInserted(itemdata.size() -1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_chat_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        int reversedPos = itemdata.size() - 1 - position;
        CommentsItems comment = itemdata.get(reversedPos);
        holder.name.setText(comment.user_name);
        holder.commentText.setText(comment.comment_text);

    }

    @Override
    public int getItemCount() {
        if (itemdata != null) {
            if (mShowAllComments) {
                return itemdata.size();
            } else if (itemdata.size() > 3) {
                return 3;
            } else {
                return itemdata.size();
            }
        } else {
            return 0;
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean ismShowAllComments() {
        return mShowAllComments;
    }

    public void setmShowAllComments(boolean mShowAllComments) {
        this.mShowAllComments = mShowAllComments;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, commentTime, commentText;
        CircleImageView user_img;
        ImageView postMenu;

        public MyViewHolder(View view) {
            super(view);
            postMenu = view.findViewById(R.id.post_menu_icon);
            name = view.findViewById(R.id.user_name);
            commentTime = view.findViewById(R.id.posted_time);
            commentText = view.findViewById(R.id.post_user_comment);
            user_img = view.findViewById(R.id.user_img);
        }
    }


}
