package com.kptech.peps.recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class HomePostCommentsAdapter extends RecyclerView.Adapter<HomePostCommentsAdapter.MyViewHolder> {
    private static final String TAG = HomePostCommentsAdapter.class.getName();
    private static final String VIEW_MENU = "View";
    private static final String EDIT_MENU = "Edit";
    private static final String DELETE_MENU = "Delete";
    private List<Comment> itemdata = new ArrayList<>();
    private Context mContext;
    private UserAccount userAccount;
    AlertDialog alertDialog;


    public void setItemList(List<Comment> data) {
        userAccount = DataHolder.getInstance().getmCurrentUser();

        if (data != null) {
            itemdata = data;
        } else {
            itemdata = new ArrayList<>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comment_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Comment comment = itemdata.get(position);
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        if (comment != null) {
            holder.name.setText(comment.getUser_name());
            holder.commentTime.setText(Utils.howLongAgo(comment.getCreated_at()));
            holder.commentText.setText(comment.getComment_text());

            if (DataValidator.isValid(comment.getUser_pic())) {
                Picasso.with(mContext).load(comment.getUser_pic()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.user_img);
            }

            if (comment.getComment_img() != null && !comment.getComment_img().equals("")) {
                holder.commentImg.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(comment.getComment_img()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.commentImg);
            } else {
                holder.commentImg.setVisibility(View.GONE);
            }
        }
        holder.postMenu.setTag(itemdata);
        holder.postMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment != null) {
                    if (comment.getComment_ID().equalsIgnoreCase(accnt.getEmail())) {
                        showpopup_own(comment,position);
                    } else showpopup_other(comment);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (itemdata != null) {
            return itemdata.size();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, commentTime, commentText;
        CircleImageView user_img;
        ImageView postMenu, commentImg;

        public MyViewHolder(View view) {
            super(view);
            postMenu = view.findViewById(R.id.post_menu_icon);
            name = view.findViewById(R.id.user_name);
            commentTime = view.findViewById(R.id.posted_time);
            commentText = view.findViewById(R.id.post_user_comment);
            commentImg = view.findViewById(R.id.comment_posted_pic);
            user_img = view.findViewById(R.id.user_img);
        }
    }

    public void showpopup_other(Comment data) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getmContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_frnd, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvReoprtUser = (TextView) dialogView.findViewById(R.id.tv_report_user);
        TextView tvBlockUser = (TextView) dialogView.findViewById(R.id.tv_block_user);
        TextView tvOffensiveContent = (TextView) dialogView.findViewById(R.id.tv_offensive_content);
        tvReoprtUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("POSTS_LISTS").child(data.getComment_img());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long reportCount = 1;

                    if (dataSnapshot.hasChild("report_user")) {
                        String offensiveContent = (String) dataSnapshot.child("report_user").getValue();
                        reportCount = Long.parseLong(offensiveContent) + 1;
                    }
                    String reoprtedByUser = userAccount.user_id;
                    myRef.child("report_user").setValue(String.valueOf(reportCount));
                    myRef.child("reported_by_users").child(reoprtedByUser).setValue(String.valueOf(reportCount));

                    Toast.makeText(mContext, "Content reported", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        });
        tvOffensiveContent.setOnClickListener(v -> {
            alertDialog.dismiss();
           /* FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("POSTS_LISTS").child(data.getRow_key());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long offensiveCount = 1;

                    if (dataSnapshot.hasChild("offensive_content")) {
                        String offensiveContent = (String) dataSnapshot.child("offensive_content").getValue();
                        offensiveCount = Long.parseLong(offensiveContent) + 1;
                    }
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("CONTENT_FEED").child(data.getRow_key());
                    myRef.child("offensive_content").setValue(String.valueOf(offensiveCount));

                    Toast.makeText(mContext, "Content marked as offensive", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
*/


        });
        tvBlockUser.setOnClickListener(v -> {
            alertDialog.dismiss();
          /*  final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("BLOCK_USER").child(userAccount.getUid());
            dataRef.child(Utils.getKey(data.getEmail())).setValue(1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Farman", "Data could not be saved " + databaseError.getMessage());
                    } else {
                        Toast.makeText(mContext, "User blocked successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            });*/
        });

        tvCancel.setOnClickListener(view12 -> alertDialog.dismiss());


        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    public void showpopup_own(Comment data, int selPos) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_user, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvEdit = (TextView) dialogView.findViewById(R.id.tvEdit);
        TextView tvDelete = (TextView) dialogView.findViewById(R.id.tvDelete);

        tvEdit.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(mContext, CreateHomePostActivity.class);
            intent.putExtra("model", new Gson().toJson(data));
            intent.putExtra("edit", true);
            mContext.startActivity(intent);
        });

        tvDelete.setOnClickListener(v -> {
            alertDialog.dismiss();
            {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        .setMessage("Are you sure, you want to delete this comment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("COMMENTS").child(data.getComment_ID());
                        }})
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                myQuittingDialogBox.show();
            }
        });

        tvCancel.setOnClickListener(v -> alertDialog.dismiss());


        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.show();
    }
}
