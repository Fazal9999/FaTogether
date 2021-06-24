package com.kptech.peps.recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kptech.peps.Notification.Token;
import com.kptech.peps.R;
import com.kptech.peps.activity.CommentsActivity;
import com.kptech.peps.activity.CreateWorldWidePostActivity;
import com.kptech.peps.activity.ImageViewActivity;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.activity.NotificationActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.ReportUserModel;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class WorldWidePostsAdapter extends RecyclerView.Adapter<WorldWidePostsAdapter.MyViewHolder> {
    private static final String TAG = WorldWidePostsAdapter.class.getName();
    AlertDialog alertDialog;
    private static final String VIEW_MENU = "View";
    private static final String EDIT_MENU = "Edit";
    private static final String DELETE_MENU = "Delete";
    private List<PostDetails> itemdata = new ArrayList<>();
    private Context mContext;
    private Drawable mUserPlaceHolder, mPostPlaceHolder;
    private UserAccount userAccount;

    public void setItemList(List<PostDetails> data) {
        userAccount = DataHolder.getInstance().getmCurrentUser();

        if (data != null) {
            itemdata = data;
        } else {
            itemdata = new ArrayList<>();
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.d(TAG,"OnCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_detail_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //  Log.d(TAG,"onBind Position is "+position);
        int reversedPos = itemdata.size() - 1 - position;
        PostDetails data = itemdata.get(position);//reverse list
        //holder.name.setText(data.get);
        String val = data.getDescription();
        if (DataValidator.isValid(val)) {
            holder.group_desc.setVisibility(View.VISIBLE);
            holder.group_desc.setText(val);

        } else {
            holder.group_desc.setVisibility(View.GONE);
            holder.group_desc.setText("");
        }

        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();


        if (data.getCreated_at() != null) {
            val = Utils.howLongAgo(data.getCreated_at());
            if (DataValidator.isValid(val)) {
                holder.line1.setText(val);
            } else {
                holder.line1.setText("");
            }
        }

        if (data.isIs_mature_content())
            holder.adult_txt.setVisibility(View.VISIBLE);
        else holder.adult_txt.setVisibility(View.GONE);

        String imageUrl = data.getPost_image();
        Log.i("worldwidePostImage",imageUrl);
        if (DataValidator.isValid(imageUrl)) {
            Picasso.with(mContext).load(imageUrl).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.groupImage);
        } else {
            holder.groupImage.setImageDrawable(mUserPlaceHolder);
        }

        Picasso.with(mContext).load(imageUrl).centerCrop().fit().error(R.drawable.post_placeholder).into(holder.postImage);
        holder.postImage.setVisibility(View.VISIBLE);
        holder.play_button.setVisibility(View.GONE);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra("img", data.getPost_image());
                mContext.startActivity(intent);
            }
        });


        holder.postMenu.setTag(reversedPos);
        holder.postMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.getEmail().equalsIgnoreCase(accnt.getEmail())) {
                    showpopup_own(data);
                } else showpopup_other(data);

            }
        });

//        try {
//            holder.tv_comment.setText(String.valueOf(data.getCommentList().size()));
//        } catch (Exception e) {
//        }
//
//        try {
//            holder.tv_like.setText(String.valueOf(data.getLikes().size()));
//        } catch (Exception e) {
//        }

        holder.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }

        });

        //set the comments part
        //List<Comment> list = data.getCommentList();

        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                //intent.putParcelableArrayListExtra("comments", (ArrayList<? extends Parcelable>) list);
                intent.putExtra("type", "worldwide");
                intent.putExtra("postmodel", new Gson().toJson(itemdata.get(position)));
                mContext.startActivity(intent);
            }
        });

        if (data.getEmail().equalsIgnoreCase(userAccount.getEmail())) {
            holder.postMore.setVisibility(View.GONE);
            holder.messages1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NotificationActivity.class);
                    intent.putExtra("post_type", data.getUser_ID());
                    mContext.startActivity(intent);
                }
            });
        }else{
            holder.messages1.setVisibility(View.GONE);
            holder.postMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateToken(FirebaseInstanceId.getInstance().getToken());

                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("user_key", data.getEmail());
                    intent.putExtra("post_type", data.getUser_ID());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (itemdata != null) {
            return itemdata.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
        mUserPlaceHolder = mContext.getResources().getDrawable(R.drawable.user_demo_dp);
        mPostPlaceHolder = mContext.getResources().getDrawable(R.drawable.post_placeholder);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, line1, group_desc, showComments;
        public CircleImageView groupImage;
        ImageView postImage, postComment, postMenu, play_button;
        public EditText mWriteComment;
        public RecyclerView mCommentsList;
        public HomePostCommentsAdapter adapter;
        public RelativeLayout postCommentsLayout;
        TextView tv_like, tv_comment, tv_url, adult_txt, postMore, messages1;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.group_name);
            line1 = view.findViewById(R.id.post_time);
            group_desc = view.findViewById(R.id.group_description);
            groupImage = view.findViewById(R.id.group_icon_id);
            tv_url = view.findViewById(R.id.tv_url);
            postImage = view.findViewById(R.id.group_post_image);
            postMenu = view.findViewById(R.id.post_menu_icon);
            showComments = view.findViewById(R.id.expand_comments);
            postCommentsLayout = view.findViewById(R.id.post_comment_layout);
            mWriteComment = view.findViewById(R.id.write_comment);
            postComment = view.findViewById(R.id.grp_post_comment);
            adult_txt = view.findViewById(R.id.group_mature);
            play_button = view.findViewById(R.id.play_button);
            tv_like = view.findViewById(R.id.tv_like);
            tv_comment = view.findViewById(R.id.tv_comment);
            mCommentsList = view.findViewById(R.id.post_comment_list);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            mCommentsList.setLayoutManager(mLayoutManager);
            postMore = view.findViewById(R.id.post_more);
        }
    }


    public void showpopup_other(PostDetails data) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getmContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_frnd, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvReoprtUser = (TextView) dialogView.findViewById(R.id.tv_report_user);
        TextView tvBlockUser = (TextView) dialogView.findViewById(R.id.tv_block_user);
        TextView tvOffensiveContent = (TextView) dialogView.findViewById(R.id.tv_offensive_content);
        tvReoprtUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            ReportUserModel reportUserModel = new ReportUserModel();
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("REPORT_USER").child(Utils.getKey(userAccount.getEmail()));
            String key = dataRef.push().getKey();
            reportUserModel.setCreated_at(ServerValue.TIMESTAMP);
            reportUserModel.setUpdated_at(ServerValue.TIMESTAMP);
            reportUserModel.setEmail(data.getEmail());
            reportUserModel.setRowKey(key);
            reportUserModel.setType("POSTS_LISTS");
            dataRef.child(key).setValue(reportUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dataRef.child(key).child("reported_by_users").child(Utils.getKey(userAccount.getEmail())).setValue(1,null);
                        Toast.makeText(mContext, "User reoprted", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }
            });

        });
        tvOffensiveContent.setOnClickListener(v -> {
            alertDialog.dismiss();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
             DatabaseReference myRef = database.getReference("POSTS_LISTS").child(data.getRow_key());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long offensiveCount = 1;

                    if (dataSnapshot.hasChild("offensive_content")) {
                        String offensiveContent = (String) dataSnapshot.child("offensive_content").getValue();
                        offensiveCount = Long.parseLong(offensiveContent) + 1;
                    }
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("POSTS_LISTS").child(data.getRow_key());
                    myRef.child("offensive_content").setValue(String.valueOf(offensiveCount));
                    myRef = database.getReference("POSTS_LISTS").child(data.getRow_key()).child("reported_by_users");
                    myRef.child(Utils.getKey(userAccount.getEmail())).setValue(1, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                    Toast.makeText(mContext, "Content marked as offensive", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        });
        tvBlockUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("BLOCK_USER").child(Utils.getKey(userAccount.getEmail()));
            dataRef.child(Utils.getKey(data.getEmail())).setValue(1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Peps", "Data could not be saved " + databaseError.getMessage());
                    } else {
                        Toast.makeText(mContext, "User blocked successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            });
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

    public void showpopup_own(PostDetails data) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_user, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvEdit = (TextView) dialogView.findViewById(R.id.tvEdit);
        TextView tvDelete = (TextView) dialogView.findViewById(R.id.tvDelete);

        tvEdit.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(mContext, CreateWorldWidePostActivity.class);
            intent.putExtra("model", new Gson().toJson(data));
            intent.putExtra("edit", true);
            mContext.startActivity(intent);

        });

        tvDelete.setOnClickListener(v -> {
            alertDialog.dismiss();
            Log.d(TAG, "DEl menu");
            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                    //set message, title, and icon
                    .setTitle("Alert")
                    .setMessage("Are you sure, you want to delete this post?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query applesQuery = ref.child("POSTS_LISTS").orderByChild("row_key").equalTo(data.getRow_key());

                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                        Toast.makeText(mContext, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            myQuittingDialogBox.show();

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


    //add for Notification
    private void updateToken(String token) {
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        String current_user_id = accnt.getEmail().replaceAll("[-+.^:,@]","");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(current_user_id).setValue(token1);
    }
}
