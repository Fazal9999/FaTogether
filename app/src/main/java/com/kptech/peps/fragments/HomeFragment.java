package com.kptech.peps.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.activity.ProfileActivity;
import com.kptech.peps.interfaces.DataChangeListener;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.HomePostsAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.PreferenceStorage;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends AppBaseFragment implements DataChangeListener {
    private static final String TAG = HomeFragment.class.getName();
    private RecyclerView mRecyclerView;
    private HomePostsAdapter mAdapter;
    private TextView empty_view;
    private List<PostDetails> postDetailsList = new ArrayList<>();
    private List<PostDetails> postDetailsListfiltered = new ArrayList<>();
    private List<UserAccount> postDetailsUsers = new ArrayList<>();
    private Map<String,List<Comment>> postDetailsComments = new HashMap<>();
    private ArrayList<Comment> postComments = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = layout.findViewById(R.id.swipe);
        empty_view=layout.findViewById(R.id.empty_view);
        initViews(layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchHomePostList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return layout;
    }

    private void initViews(View layout) {
        mRecyclerView = layout.findViewById(R.id.recycler_view);
        mAdapter = new HomePostsAdapter();
        mAdapter.setmContext(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemList(postDetailsListfiltered);
        mAdapter.setUserList(postDetailsUsers);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchHomePostList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDataChanged() {
        Log.d(TAG, "onDataChanged ");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void fetchHomePostList() {
        showProgressDialog(null, null);
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postDetailsList.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    PostDetails info = childDataSnapshot.getValue(PostDetails.class);
                    if (info != null) {
                        if (Utils.preferences.getBoolean("hideMature", false)) {
                            if (!info.isIs_mature_content()) {
                                postDetailsList.add(info);
                            }
                        } else {
                            postDetailsList.add(info);
                        }
                    }
                }

                if (postDetailsList.size() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }else {
                    empty_view.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                Collections.reverse(postDetailsList);
                mAdapter.setItemList(postDetailsList);
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
                postDetailsUsers.clear();
                for (PostDetails post : postDetailsList) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        UserAccount user = childDataSnapshot.getValue(UserAccount.class);
                        if(user != null){
                            if (post.getEmail().equals(user.getEmail())) {
                                postDetailsUsers.add(user);
                                break;
                            }
                        }
                    }
                }

                mAdapter.setUserList(postDetailsUsers);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference cmntRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMMENTS);
        cmntRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot children : childDataSnapshot.getChildren()) {
                       Comment cmnt = children.getValue(Comment.class);
                       if (cmnt != null) {
                           if (cmnt.getComment_ID().equals(children.getKey())){
                               postComments.add(cmnt);
                           }
                       }
                    }
                    if (!postDetailsComments.entrySet().contains(childDataSnapshot.getKey())) {
                        postDetailsComments.put(childDataSnapshot.getKey(), postComments);
                    }
                }
                mAdapter.setCommentList(postDetailsComments);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}