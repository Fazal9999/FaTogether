package com.kptech.peps.activity;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import com.kptech.peps.recycler.HomePostsAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kptech.peps.R;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.HomePostsAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.DataValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    protected KProgressHUD mProgressDialog;
    private List<PostDetails> postDetailsList = new ArrayList<>();
    private List<UserAccount> postUserList = new ArrayList<>();
    private List<PostDetails> searchList = new ArrayList<>();
    private List<UserAccount> searchUserList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RecyclerView mRecyclerView;
    private TextView empty_view;
    private HomePostsAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecyclerView = findViewById(R.id.search_list);
        empty_view = findViewById(R.id.empty_search);
        SearchView searchView = findViewById(R.id.simpleSearchView);
        postAdapter = new HomePostsAdapter();
        postAdapter.setmContext(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(postAdapter);
        fetchPostList();

        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                for(PostDetails post: postDetailsList) {
                    if (post.getDescription().toLowerCase().contains(query.toLowerCase())) {
                        searchList.add(post);
                    }
                }

                for (UserAccount user: postUserList) {
                    for (int i = 0; i < searchList.size(); i++) {
                        if (user.getEmail().equals(searchList.get(i).getEmail())) {
                            searchUserList.add(user);
                        }
                    }
                }

                if (searchList.size() > 0) {
                    empty_view.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    empty_view.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

                postAdapter.setItemList(searchList);
                postAdapter.setUserList(searchUserList);
                postAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                if(newText.equals("")) {
                    empty_view.setVisibility(View.VISIBLE);
                    searchList.clear();
                    searchUserList.clear();
                }
                return false;
            }
        });

    }

    private void fetchPostList() {
        showProgressDialog(null, null);
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postDetailsList.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    PostDetails info = childDataSnapshot.getValue(PostDetails.class);
                    if (info != null) {
                        postDetailsList.add(info);
                    }
                }
                Collections.reverse(postDetailsList);
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cancelProgressDialog();
            }
        });

        DatabaseReference userRef = database.getReference(FirebaseConstants.USERS);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postUserList.clear();
                for (int i = 0; i < postDetailsList.size(); i++) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        try {
                            UserAccount user = childDataSnapshot.getValue(UserAccount.class);
                            if(user != null){
                                if (postDetailsList.get(i).getEmail().equals(user.getEmail())) {
                                    postUserList.add(user);
                                }
                            }
                        } catch (Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    protected void showProgressDialog(String title, String msg){
        mProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        if(DataValidator.isValid(title)){
            mProgressDialog.setLabel(title);
        }
        if(DataValidator.isValid(msg)){
            mProgressDialog.setDetailsLabel(msg);
        }
        mProgressDialog.show();
    }

    protected void cancelProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}