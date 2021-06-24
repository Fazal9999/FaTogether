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
import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.activity.CommentsActivity;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.activity.CreateWorldWidePostActivity;
import com.kptech.peps.activity.ImageViewActivity;
import com.kptech.peps.activity.NotificationActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.ReportUserModel;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyViewHolder> {
    private static final String TAG = MyPostsAdapter.class.getName();
    private static final String VIEW_MENU = "View";
    private static final String EDIT_MENU = "Edit";
    AlertDialog alertDialog;
    private static final String DELETE_MENU = "Delete";
    private List<PostDetails> itemData = new ArrayList<>();
    private Context mContext;
    private Drawable mUserPlaceHolder, mPostPlaceHolder;
    private UserAccount userAccount;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String HOME_TYPE = "homeFeed";
    private static final String WORLD_WIDE_TYPE = "worldwideFeed";

    public void setItemList(List<PostDetails> data) {
        userAccount = DataHolder.getInstance().getmCurrentUser();
        if (data != null) {
            itemData = data;
        } else {
            itemData = new ArrayList<>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.d(TAG,"OnCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_detail_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //  Log.d(TAG,"onBind Position is "+position);
        int reversedPos =  position;
        PostDetails data = itemData.get(position);//reverse list
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
                holder.postTime.setText(val);
            } else {
                holder.postTime.setText("");
            }
        }

        if (data.isIs_mature_content())
            holder.matureTV.setVisibility(View.VISIBLE);
        else holder.matureTV.setVisibility(View.GONE);

//        String imageUrl = data.getUser_image_url();
        String post_type = data.getUser_ID();
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
                    showPopUp_Own(data);
                } else showpopup_other(data);
            }
        });

        if (data.getComments()){
            holder.tv_comment.setText("HAS COMMENTS");
        } else {
            holder.tv_comment.setText("0");
        }
        try {
            holder.tv_like.setText(String.valueOf(data.getPost_likes()));
        } catch (Exception e) {
        }

//        holder.tv_like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PostDetails postDetails = itemData.get(position);
//                if (!postDetails.isDidILiked()) {
//                        String tag = postDetails.getRow_key();
//                        Like like = new Like();
//                    UserAccount account = DataHolder.getInstance().getmCurrentUser();
//
//                    if (account != null) {
//                        like.setCreated_at(System.currentTimeMillis());
//                        like.setUpdated_at(System.currentTimeMillis());
//                        like.setEmail(account.getEmail());
//                        String name = "";
//                        if (account.getFirst_name() != null) {
//                            name += account.getFirst_name();
//                        }
//                        if (account.getLast_name() != null) {
//                            name += " " + account.getLast_name();
//                        }
//                        like.setUser_name(name);
//                        like.setUser_id(Utils.getKey(account.getEmail()));
//                        BackendServer.getInstance().postLike(like, tag,Utils.getKey(account.getEmail()));
//                        holder.tv_like.setText(String.valueOf(itemData.get(position).getLikes().size()+1));
//                       notifyItemChanged(position);
//                    }
//                }
//            }
//        });

        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                //intent.putParcelableArrayListExtra("comments", (ArrayList<? extends Parcelable>) list);
                intent.putExtra("postmodel", new Gson().toJson(itemData.get(position)));
                mContext.startActivity(intent);
            }
        });

        holder.postMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NotificationActivity.class);
                intent.putExtra("post_type", post_type);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (itemData != null) {
            return itemData.size();
        } else {
            return 0;
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
        mUserPlaceHolder = mContext.getResources().getDrawable(R.drawable.ic_user_pic);
        mPostPlaceHolder = mContext.getResources().getDrawable(R.drawable.post_placeholder);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, postTime, group_desc, showComments;
        public CircleImageView groupImage;
        ImageView postImage, postComment, postMenu,play_button;
        public EditText mWriteComment;
        public RecyclerView mCommentsList;
        public HomePostCommentsAdapter adapter;
        public RelativeLayout postCommentsLayout;
        TextView tv_like, tv_comment, matureTV, tv_username, postMore;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.group_name);
            tv_username = view.findViewById(R.id.group_username);
            play_button=view.findViewById(R.id.play_button);
            postTime = view.findViewById(R.id.post_time);
            matureTV =view.findViewById(R.id.group_mature);
            group_desc = view.findViewById(R.id.group_description);
            groupImage = view.findViewById(R.id.group_icon_id);
            postImage = view.findViewById(R.id.group_post_image);
            postMenu = view.findViewById(R.id.post_menu_icon);
            showComments = view.findViewById(R.id.expand_comments);
            postCommentsLayout = view.findViewById(R.id.post_comment_layout);
            mWriteComment = view.findViewById(R.id.write_comment);
            postComment = view.findViewById(R.id.grp_post_comment);
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
        TextView tvReportUser = (TextView) dialogView.findViewById(R.id.tv_report_user);
        TextView tvBlockUser = (TextView) dialogView.findViewById(R.id.tv_block_user);
        TextView tvOffensiveContent = (TextView) dialogView.findViewById(R.id.tv_offensive_content);
        tvReportUser.setOnClickListener(v -> {
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
            DatabaseReference myRef = database.getReference("POSTS").child(data.getRow_key());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long offensiveCount = 1;

                    if (dataSnapshot.hasChild("offensive_content")) {
                        String offensiveContent = (String) dataSnapshot.child("offensive_content").getValue();
                        offensiveCount = Long.parseLong(offensiveContent) + 1;
                    }
                    DatabaseReference myRef = database.getReference("POSTS").child(data.getRow_key());
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

    public void showPopUp_Own(PostDetails data) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_user, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvEdit = (TextView) dialogView.findViewById(R.id.tvEdit);
        TextView tvDelete = (TextView) dialogView.findViewById(R.id.tvDelete);

        tvEdit.setOnClickListener(v -> {
            alertDialog.dismiss();
            {
                if (data.getUser_ID().equalsIgnoreCase(HOME_TYPE)) {
                    Intent intent = new Intent(mContext, CreateHomePostActivity.class);
                    intent.putExtra("model",new Gson().toJson(data));
                    intent.putExtra("edit",true);
                    mContext.startActivity(intent);

                } else if (data.getUser_ID().equalsIgnoreCase(WORLD_WIDE_TYPE)) {
                    Intent intent = new Intent(mContext, CreateWorldWidePostActivity.class);
                    intent.putExtra("model",new Gson().toJson(data));
                    intent.putExtra("edit",true);
                    mContext.startActivity(intent);

                }
                Log.d(TAG, "Edit menu");
            }  });
        tvDelete.setOnClickListener(v -> {
            {
                alertDialog.dismiss();
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

            }        });

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
