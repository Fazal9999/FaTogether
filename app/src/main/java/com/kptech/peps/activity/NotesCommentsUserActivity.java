package com.kptech.peps.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.NotesComment;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.model.UserNotes;
import com.kptech.peps.recycler.ChatCommentUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotesCommentsUserActivity extends AppBaseActivity {
    ChatCommentUserAdapter adapter;
    RecyclerView recyclerView;
    EditText mWriteComment;
    ImageView postComment;
    List<NotesComment> userList = new ArrayList<>();
    UserNotes postDetails = new UserNotes();
    private List<UserNotes> postDetailsList;
    String user_name = "", row_id = "";
    String postatype = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_comments);
        mWriteComment = findViewById(R.id.write_comment);
        recyclerView = findViewById(R.id.post_comment_list);
        postComment = findViewById(R.id.grp_post_comment);
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        setHeaderView("User List");
        postDetails = new Gson().fromJson(getIntent().getStringExtra("data"), UserNotes.class);

        try {
            userList.addAll(postDetails.getNotesComment());
        } catch (Exception e) {

        }

        if (userList.size() == 0)
            return;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatCommentUserAdapter(postDetails);
        adapter.setItemList(userList);
        adapter.setmContext(this);
        adapter.setmShowAllComments(true);
        recyclerView.setAdapter(adapter);
    }
}
