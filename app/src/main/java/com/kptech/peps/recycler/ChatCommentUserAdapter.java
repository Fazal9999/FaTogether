package com.kptech.peps.recycler;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.activity.NotesCommentsActivity;
import com.kptech.peps.model.NotesComment;
import com.kptech.peps.model.UserNotes;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class ChatCommentUserAdapter extends RecyclerView.Adapter<ChatCommentUserAdapter.MyViewHolder> {
    private List<NotesComment> itemdata = new ArrayList<>();
    private Context mContext;
    private boolean mShowAllComments;
    UserNotes postDetails = new UserNotes();

    public ChatCommentUserAdapter(UserNotes postDetails) {
        this.postDetails = postDetails;
    }

    public void setItemList(List<NotesComment> data) {
        if (data != null) {
            itemdata = data;
        } else {
            itemdata = new ArrayList<>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_chat_comment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        int reversedPos = itemdata.size() - 1 - position;
        NotesComment comment = itemdata.get(reversedPos);
        holder.name.setText(comment.first_name + comment.first_name);

        if (DataValidator.isValid(comment.profile_url)) {
            Picasso.with(mContext).load(comment.profile_url).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.user_img);
        }
        holder.containner.setOnClickListener(view -> {
            Log.d("Farman","clicked");
            Intent intent = new Intent(mContext, NotesCommentsActivity.class);
            intent.putExtra("data", new Gson().toJson(postDetails));
            intent.putExtra("commenter_id", Utils.getKey(comment.email));
            mContext.startActivity(intent);

        });
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
        LinearLayout containner;

        public MyViewHolder(View view) {
            super(view);
            postMenu = view.findViewById(R.id.post_menu_icon);
            name = view.findViewById(R.id.user_name);
            commentTime = view.findViewById(R.id.posted_time);
            commentText = view.findViewById(R.id.post_user_comment);
            user_img = view.findViewById(R.id.user_img);
            containner = view.findViewById(R.id.contaier);
        }
    }


}
