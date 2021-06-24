package com.kptech.peps.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.activity.CreateNewsPostActivity;
import com.kptech.peps.activity.CreatePodcastActivity;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.activity.CreateWorldWidePostActivity;
import com.kptech.peps.activity.CreatecontentPostActivity;
import com.kptech.peps.activity.ProfileActivity;
import com.kptech.peps.interfaces.DataChangeListener;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.WorldWidePostsAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorldwideFragment extends AppBaseFragment implements DataChangeListener {
    private static final String TAG = WorldwideFragment.class.getName();
    private RecyclerView mRecyclerView;
    private WorldWidePostsAdapter mAdapter;
    private TextView empty_view;
    private List<PostDetails> postDetailsList = new ArrayList<>();
    private List<PostDetails> postDetailsListfiltered = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;

    public WorldwideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_worldwide, container, false);
        initViews(layout);
        swipeRefreshLayout = layout.findViewById(R.id.swipe);
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
        TextView mPostLayout = layout.findViewById(R.id.what_is_layout);
        mRecyclerView = layout.findViewById(R.id.recycler_view);
        mAdapter = new WorldWidePostsAdapter();
        mAdapter.setmContext(getActivity());
        empty_view = layout.findViewById(R.id.empty_view);
        mPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bottomSheet();
                UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
                if (!accnt.getIsC2G()) {
                    Toast.makeText(getContext(), "Worldwide account not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), CreateWorldWidePostActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemList(postDetailsList);
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                postDetailsListfiltered.clear();
//                for (PostDetails postDetails : postDetailsList) {
//                    if (adult_check.isChecked()) {
//                        if ((postDetails.getUser_id().equalsIgnoreCase("worldwideFeed") || postDetails.getUser_id().equalsIgnoreCase("newsAccountFeed") ||
//                                postDetails.getUser_id().equalsIgnoreCase("contentFeed")||
//                                postDetails.getUser_id().equalsIgnoreCase("podcastFeed")));
//                    } else {
//                        if ((postDetails.getUser_id().equalsIgnoreCase("worldwideFeed") || postDetails.getUser_id().equalsIgnoreCase("newsAccountFeed") ||
//                                postDetails.getUser_id().equalsIgnoreCase("contentFeed")||
//                                postDetails.getUser_id().equalsIgnoreCase("podcastFeed")) && (!postDetails.isIs_mature_content()))
//                            if (postDetails.getStart_at().equals(postDetails.getEnd_at())) {
//                                postDetailsListfiltered.add(postDetails);
//                            }else{
//                                if (postDetails.getStart_at()<System.currentTimeMillis() && System.currentTimeMillis()<postDetails.getEnd_at()) {
//                                    postDetailsListfiltered.add(postDetails);
//                                }
//                            }
//                    }
//                }

//                Collections.sort(postDetailsListfiltered, new Comparator<PostDetails>() {
//                    @Override
//                    public int compare(PostDetails comment, PostDetails t1) {
//                        if (comment.getCreated_at() > t1.getCreated_at())
//                            return -1;
//                        else if (comment.getCreated_at() < t1.getCreated_at())
//                            return 1;
//                        else return 0;
//
//                    }
//                });
                if (postDetailsListfiltered.size() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                mAdapter.setItemList(postDetailsListfiltered);
                mAdapter.notifyDataSetChanged();
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgressDialog(null, null);
            }
        });
    }

    public void bottomSheet() {

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_sheet);
        TextView cancel_action = dialog.findViewById(R.id.cancel_action);
        TextView home_post = dialog.findViewById(R.id.home_post);
        TextView world_post = dialog.findViewById(R.id.world_post);
        TextView news_post = dialog.findViewById(R.id.news_post);
        TextView content_post = dialog.findViewById(R.id.content_post);
        TextView podcast_post = dialog.findViewById(R.id.podcast_post);

        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        cancel_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        home_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                Intent intent = new Intent(getActivity(), CreateHomePostActivity.class);
                startActivity(intent);
            }
        });
        world_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!accnt.getIsC2G()) {
                    Toast.makeText(getContext(), "Worldwide account not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
//                Intent intent = new Intent(getActivity(), CreateWorldWidePostActivity.class);
//                startActivity(intent);
            }
        });
        dialog.show();
    }


}
