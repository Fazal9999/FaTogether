package com.kptech.peps.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.model.CommentsItems;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.NotesComment;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.model.UserNotes;
import com.kptech.peps.recycler.ChatCommentListAdapter;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotesCommentsActivity extends AppBaseActivity {
    ChatCommentListAdapter adapter;
    RecyclerView recyclerView;
    EditText mWriteComment;
    ImageView postComment;
    TextView empty_view;
    List<CommentsItems> cmntList = new ArrayList<>();
    UserNotes postDetails = new UserNotes();
    String commenterId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_comments);
        empty_view=findViewById(R.id.empty_view);
        mWriteComment = findViewById(R.id.write_comment);
        recyclerView = findViewById(R.id.post_comment_list);
        postComment = findViewById(R.id.grp_post_comment);
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        postDetails = new Gson().fromJson(getIntent().getStringExtra("data"), UserNotes.class);
        commenterId = getIntent().getStringExtra("commenter_id");

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tag = postDetails.getRow_Key();

                NotesComment comment = new NotesComment();
                UserAccount account = DataHolder.getInstance().getmCurrentUser();

                if (account != null) {

                    CommentsItems items = new CommentsItems();
                    items.comment_text = (mWriteComment.getText().toString());
                    items.created_at = System.currentTimeMillis();
                    items.updated_at = System.currentTimeMillis();
                    items.email = accnt.getEmail();
                    try {
                        items.user_name = accnt.getFirst_name() + accnt.getLast_name();
                    } catch (Exception e) {
                        items.user_name = accnt.getFirst_name();
                    }

                    HashMap<String, CommentsItems> mapOfPosts = new HashMap<>();
                    mapOfPosts.put(tag, items);

                    HashMap<String, Object> userDetails = new HashMap<>();
                    userDetails.put("email", account.getEmail());
                    userDetails.put("first_name", account.getFirst_name());
                    userDetails.put("last_name", account.getLast_name());
                  //  userDetails.put("user_id", account.getUid());
                   // userDetails.put("profile_url", account.getImage_url());
                   /* comment.email = (account.getEmail());
                    comment.first_name = (accnt.getFirst_name());
                    comment.last_name = accnt.getLast_name();
                    comment.user_id = accnt.getUid();
                    comment.profile_url = accnt.getImage_url();*/

                    comment.setComments(mapOfPosts);

                    BackendServer.getInstance().postPOstComment(items, userDetails, tag, commenterId);
                    mWriteComment.setText("");

                }
            }

        });
        setHeaderView("All Comments");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatCommentListAdapter();
        adapter.setmContext(this);
        adapter.setmShowAllComments(true);
        recyclerView.setAdapter(adapter);
        fetchCommentList();


    }


    private void fetchCommentList() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("NOTES").child(postDetails.getRow_Key()).child("notes_users").child(commenterId).child("comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentsItems data = dataSnapshot.getValue(CommentsItems.class);
                adapter.setItemList(data);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }
}
