package com.kptech.peps.recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
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
import com.kptech.peps.activity.NotesCommentsActivity;
import com.kptech.peps.activity.NotesCommentsUserActivity;
import com.kptech.peps.activity.ProfileActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.ReportUserModel;
import com.kptech.peps.model.UserAccount;
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

public class UserNotesAdapter extends RecyclerView.Adapter<UserNotesAdapter.MyViewHolder> {
    private static final String TAG = UserNotesAdapter.class.getName();
    AlertDialog alertDialog;
    private List<UserNotes> itemdata = new ArrayList<>();
    private Context mContext;
    private Drawable mUserPlaceHolder;
    private UserAccount userAccount;

    public void setItemList(List<UserNotes> data) {
        userAccount = DataHolder.getInstance().getmCurrentUser();

        if (data != null) {
            itemdata = data;
        } else {
            itemdata = new ArrayList<>();
        }
    }

    public UserNotes getSelectedPositionItem(int pos) {
        if (pos < itemdata.size()) {
            return itemdata.get(pos);
        } else {
            return null;
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.d(TAG,"OnCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //  Log.d(TAG,"onBind Position is "+position);
        UserNotes data = itemdata.get(position);//reverse the list
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        String val = data.getNotesFrom();
        if (DataValidator.isValid(val)) {
            holder.name.setText(val);
        }
        holder.postMenu.setTag(position);
        val = "Lookingfor ";
        if (DataValidator.isValid(data.getLookingFor())) {
            int startIndex = val.length();
            val += data.getLookingFor();
            Spannable span1 = Utils.getSpannableWithFont(mContext, val, startIndex, val.length(), Color.BLACK);
            holder.line1.setText(TextUtils.concat(span1));
        } else {
            holder.line1.setText("N/A");
        }


        val = "";
        if (DataValidator.isValid(data.getiAm())) {
            val += data.getiAm();

        }
        if (DataValidator.isValid(data.getAge())) {
            val += ", " + data.getAge();
        }
        holder.line2.setText(val);

        val = data.getNotes();
        if (DataValidator.isValid(val)) {
            holder.notesTxt.setText(val);
        } else {
            holder.notesTxt.setText("");
        }

        String imageUrl = data.getImageUrl();
        if (DataValidator.isValid(imageUrl)) {
            Picasso.with(mContext).load(imageUrl).centerCrop().fit().error(R.drawable.post_placeholder).into(holder.user_img);
        } else {
            holder.user_img.setImageDrawable(mUserPlaceHolder);
        }
        holder.user_img.setOnClickListener(view -> {
            if (data.allowOther == 0)
                return;
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("type", "notes");
            intent.putExtra("acc", new Gson().toJson(data));
            mContext.startActivity(intent);
        });

        holder.ic_comments.setOnClickListener(view -> {
            if (data.getUserKey().equalsIgnoreCase(accnt.getEmail())) {
                if (data.notes_users == null) {
                    Toast.makeText(mContext, "No Comments Yet", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (data.getNotesComment().size() == 0) {
                    Toast.makeText(mContext, "No Comments Yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext, NotesCommentsUserActivity.class);
                intent.putExtra("data", new Gson().toJson(data));
                mContext.startActivity(intent);
            } else {

                Intent intent = new Intent(mContext, NotesCommentsActivity.class);
                intent.putExtra("data", new Gson().toJson(data));
                intent.putExtra("commenter_id", Utils.getKey(accnt.getEmail()));
                mContext.startActivity(intent);
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
        mUserPlaceHolder = mContext.getResources().getDrawable(R.drawable.post_placeholder);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, line1, line2, notesTxt;
        CircleImageView user_img;
        ImageView ic_comments, postMenu;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            postMenu = view.findViewById(R.id.post_menu_icon);
            line1 = view.findViewById(R.id.line1);
            ic_comments = view.findViewById(R.id.ic_comments);
            line2 = view.findViewById(R.id.line2);
            notesTxt = view.findViewById(R.id.notes_description);
            user_img = view.findViewById(R.id.profile_img);

        }
    }

    public void showpopup_other(UserNotes data) {
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
            reportUserModel.setEmail(data.getUserKey());
            reportUserModel.setRowKey(key);
            reportUserModel.setType("USERNOTES");
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
            final DatabaseReference myRef = database.getReference("USERNOTES").child(data.getRow_Key());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long offensiveCount = 1;

                    if (dataSnapshot.hasChild("offensive_content")) {
                        String offensiveContent = (String) dataSnapshot.child("offensive_content").getValue();
                        offensiveCount = Long.parseLong(offensiveContent) + 1;
                    }
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("USERNOTES").child(data.getRow_Key());
                    myRef.child("offensive_content").setValue(String.valueOf(offensiveCount));
                    myRef = database.getReference("USERNOTES").child(data.getRow_Key()).child("reported_by_users");
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
            dataRef.child(Utils.getKey(data.getUserKey())).setValue(1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Farman", "Data could not be saved " + databaseError.getMessage());
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

    public void showpopup_own(UserNotes data) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_user, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvEdit = (TextView) dialogView.findViewById(R.id.tvEdit);
        TextView tvDelete = (TextView) dialogView.findViewById(R.id.tvDelete);

        tvEdit.setOnClickListener(v -> {
            alertDialog.dismiss();
         /*   Intent intent = new Intent(mContext, CreateHomePostActivity.class);
            intent.putExtra("model", new Gson().toJson(data));
            intent.putExtra("edit", true);
            mContext.startActivity(intent);
     */
        });

        tvDelete.setOnClickListener(v -> {
            alertDialog.dismiss();
            {
                Log.d(TAG, "DEl menu");

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        //set message, title, and icon
                        .setTitle("Alert")
                        .setMessage("Are you sure, you want to delete this post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query applesQuery = ref.child("USERNOTES").orderByChild("row_key").equalTo(data.getRow_Key());

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
