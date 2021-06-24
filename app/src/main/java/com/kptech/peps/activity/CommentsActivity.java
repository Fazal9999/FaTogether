package com.kptech.peps.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.model.C2GUser;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.HomePostCommentsAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppBaseActivity {

    HomePostCommentsAdapter adapter;
    RecyclerView recyclerView;
    EditText mWriteComment;
    ImageView postComment, uploadPic, commentPic;
    ArrayList<Comment> commentArrayList = new ArrayList<>();
    String row_id="", commentKey, todayString, commentID;
    Comment newComment;
    RelativeLayout commentView;
    private Uri contentUri;
    private static final int REQUEST_PHOTO = 0x0111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mWriteComment = findViewById(R.id.write_comment);
        recyclerView = findViewById(R.id.post_comment_list);
        postComment = findViewById(R.id.grp_post_comment);
        uploadPic = findViewById(R.id.upload_pic);
        commentPic = findViewById(R.id.comment_pic);
        commentView = findViewById(R.id.comment_pic_view);

        adapter = new HomePostCommentsAdapter();
        adapter.setmContext(this);
        recyclerView.setAdapter(adapter);

        row_id = getIntent().getStringExtra("postModel");
        UserAccount account = DataHolder.getInstance().getmCurrentUser();
        commentKey = Utils.randomString(7);
        todayString = Utils.getTodayString(this);
        commentID = todayString + " | " + commentKey;

        getComments();

        setHeaderView("Comments");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //FIX TO ADD COMMENTS TO CURRENT NODE IN NEW DATABASE
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newComment = new Comment();

                if (!mWriteComment.getText().toString().equals("")) {
                    if (account != null) {
                        commentKey = Utils.randomString(7);
                        todayString = Utils.getTodayString(CommentsActivity.this);
                        newComment = new Comment();
                        commentID = todayString + " | " + commentKey;

                        newComment.setCreated_at(todayString);
                        newComment.setUser_id(Utils.getKey(account.getEmail()));
                        newComment.setUser_name(account.getUser_id());
                        newComment.setUser_pic(account.getProfile_pic());
                        newComment.setComment_text(mWriteComment.getText().toString());
                        newComment.setComment_ID(todayString + " | " + commentKey);
                        if (contentUri != null) {
                            newComment.setComment_img(String.valueOf(contentUri));
                        }
                        commentArrayList.add(newComment);
                        adapter.notifyDataSetChanged();
                        sendCommentsList();
                        commentsOn();

                        mWriteComment.setText("");
                        commentKey = "";
                        todayString = "";
                        newComment = new Comment();
                        commentID = "";
                        contentUri = null;
                        commentView.setVisibility(View.GONE);
                    }
                }
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabPicForComment();
            }
        });
    }

    private void getComments(){
        commentArrayList.clear();
        showProgressDialog(null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseConstants.COMMENTS).child(row_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment data = snapshot.getValue(Comment.class);
                    if (data != null) {
                        commentArrayList.add(data);
                    }
                }

                adapter.setItemList(commentArrayList);
                adapter.notifyDataSetChanged();
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void grabPicForComment() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(CommentsActivity.this);
        pictureDialog.setTitle("Select Image From");
        String[] pictureDialogItems = {
                "Gallery"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestForPermission();
                    }
                });
        pictureDialog.show();
    }

    private void requestForPermission() {
        //get permission if we do not have them yet
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if we don't have it.
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PHOTO);
        } else {
            // open gallery
            Intent pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(pickPhoto , 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == 0) {
            // open gallery
            Intent pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(pickPhoto, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            contentUri = data.getData();
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 4;
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                commentView.setVisibility(View.VISIBLE);
                commentPic.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommentsList() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMMENTS);
        myRef.child(row_id).child(commentID).setValue(newComment);
    }

    private void commentsOn(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.POSTS_LIST);
        myRef.child(row_id).child("comments").setValue(true);
    }
}
