package com.kptech.peps.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kptech.peps.R;
import com.kptech.peps.activity.CreateNewsPostActivity;
import com.kptech.peps.activity.CreatePodcastActivity;
import com.kptech.peps.activity.CreateHomePostActivity;
import com.kptech.peps.activity.CreateWorldWidePostActivity;
import com.kptech.peps.activity.CreatecontentPostActivity;
import com.kptech.peps.activity.HomeActivity;
import com.kptech.peps.activity.ProfileActivity;
import com.kptech.peps.interfaces.DataChangeListener;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.PostsDataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.MyPostsAdapter;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostFragment extends AppBaseFragment implements DataChangeListener {
    private static final String TAG = MyPostFragment.class.getName();

    private static final String HOME_TYPE = "homeFeed";
    private static final String WORLD_WIDE_TYPE = "worldwideFeed";
    private static final String NEWS_TYPE = "newsAccountFeed";
    private static final String CONTENT_TYPE = "contentFeed";
    private static final String PODCAST_TYPE = "podcastFeed";
    private RecyclerView mRecyclerView;
    private MyPostsAdapter mAdapter;
    private TextView mPostLayout = null;
    private TextView mProfileView = null;
    private List<PostDetails> postDetailsList = new ArrayList<>();
    private List<PostDetails> postDetailsListfiltered = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private String mCurrentSelType = null;
    TextView empty_view;
    String pageType = "home";
    boolean tab3, tab4, tab5;

    public static ViewPager viewPager;

    public MyPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_myposthome, container, false);
        initViews(layout);
        swipeRefreshLayout = layout.findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchPosts(mCurrentSelType);
            swipeRefreshLayout.setRefreshing(false);
        });
requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
    @Override
    public void handleOnBackPressed() {

Intent intent= new Intent(requireActivity(), HomeActivity.class);
startActivity(intent);
requireActivity().finish();

    }
});
        return layout;
    }

    private void initViews(View layout) {
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Post");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
       ActionBar supportActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
       if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getActivity().onBackPressed();
                Intent intent=new Intent(requireActivity(),HomeActivity.class);
                startActivity(intent);
                requireActivity().finish();

            }
        });
        empty_view = layout.findViewById(R.id.empty_view);
        TabLayout tabLayout = layout.findViewById(R.id.group_tabs);
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab position is " + tab.getPosition());

                if (tab3){
                    if (tab4){
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {

                                pageType = "newsAccountFeed";
                                mCurrentSelType = NEWS_TYPE;
                                fetchPostsByTpe(NEWS_TYPE);
                                mProfileView.setText("NewsSite Profile");
                            } else if (tab.getPosition() == 3) {
                                pageType = "content";
                                mCurrentSelType = CONTENT_TYPE;
                                fetchPostsByTpe(CONTENT_TYPE);
                                mProfileView.setText("Content Account Profile");
                            } else if (tab.getPosition() == 4) {

                                pageType = "podcast";
                                mCurrentSelType = PODCAST_TYPE;
                                fetchPostsByTpe(PODCAST_TYPE);
                                mProfileView.setText("Podcast Profile");
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {
                                pageType = "newsAccountFeed";
                                mCurrentSelType = NEWS_TYPE;
                                fetchPostsByTpe(NEWS_TYPE);
                                mProfileView.setText("NewsSite Profile");
                            } else if (tab.getPosition() == 3) {
                                pageType = "content";
                                mCurrentSelType = CONTENT_TYPE;
                                fetchPostsByTpe(CONTENT_TYPE);
                                mProfileView.setText("Content Account Profile");
                            }
                        }
                    }else{
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {

                                pageType = "newsAccountFeed";
                                mCurrentSelType = NEWS_TYPE;
                                fetchPostsByTpe(NEWS_TYPE);
                                mProfileView.setText("NewsSite Profile");
                            } else if (tab.getPosition() == 3) {
                                pageType = "podcast";
                                mCurrentSelType = PODCAST_TYPE;
                                fetchPostsByTpe(PODCAST_TYPE);
                                mProfileView.setText("Podcast Profile");
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {

                                pageType = "newsAccountFeed";
                                mCurrentSelType = NEWS_TYPE;
                                fetchPostsByTpe(NEWS_TYPE);
                                mProfileView.setText("NewsSite Profile");
                            }
                        }
                    }
                }else{
                    if (tab4){
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {
                                pageType = "content";
                                mCurrentSelType = CONTENT_TYPE;
                                fetchPostsByTpe(CONTENT_TYPE);
                                mProfileView.setText("Content Account Profile");
                            } else if (tab.getPosition() == 3) {

                                pageType = "podcast";
                                mCurrentSelType = PODCAST_TYPE;
                                fetchPostsByTpe(PODCAST_TYPE);
                                mProfileView.setText("Podcast Profile");
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {

                                pageType = "content";
                                mCurrentSelType = CONTENT_TYPE;
                                fetchPostsByTpe(CONTENT_TYPE);
                                mProfileView.setText("Content Account Profile");
                            } else if (tab.getPosition() == 3) {

                            }
                        }
                    }else{
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            } else if (tab.getPosition() == 2) {
                                pageType = "podcast";
                                mCurrentSelType = PODCAST_TYPE;
                                fetchPostsByTpe(PODCAST_TYPE);
                                mProfileView.setText("Podcast Profile");
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                pageType = "home";
                                fetchPostsByTpe(HOME_TYPE);
                                mProfileView.setText("Home Profile");
                            } else if (tab.getPosition() == 1) {
                                pageType = "worldwide";
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchPostsByTpe(WORLD_WIDE_TYPE);
                                mProfileView.setText("Worldwide Profile");
                            }
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mCurrentSelType = HOME_TYPE;

        mPostLayout = layout.findViewById(R.id.new_post);
        mProfileView = layout.findViewById(R.id.profile);
        mRecyclerView = layout.findViewById(R.id.recycler_view);
        mAdapter = new MyPostsAdapter();
        mAdapter.setmContext(getActivity());

        mPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

                if (mCurrentSelType.equalsIgnoreCase(HOME_TYPE)) {
                    Intent intent = new Intent(getActivity(), CreateHomePostActivity.class);
                    startActivity(intent);
                } else if (mCurrentSelType.equalsIgnoreCase(WORLD_WIDE_TYPE)) {
                    if (!accnt.getIsC2G()) {
                        Toast.makeText(getContext(), "Worldwide account not available", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Intent intent = new Intent(getActivity(), CreateWorldWidePostActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        mProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("type", pageType);
                Log.d(TAG, pageType);
                startActivity(intent);
            }
        });

        if (postDetailsListfiltered.size() == 0) {
            empty_view.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            empty_view.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemList(postDetailsList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchPosts(mCurrentSelType);
        fetchPostsByTpe(mCurrentSelType);
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        fetchPosts(mCurrentSelType);
        fetchPostsByTpe(mCurrentSelType);
    }
*/

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

    private void setPostList(String type) {
        List<PostDetails> list = PostsDataHolder.getInstance().getUserPostsByType(type);
        if (list == null) {
            list = new ArrayList<>();
        }
//        Collections.sort(list, new Comparator<PostDetails>() {
//            @Override
//            public int compare(PostDetails comment, PostDetails t1) {
//                if (comment.getCreated_at() > t1.getCreated_at())
//                    return -1;
//                else if (comment.getCreated_at() < t1.getCreated_at())
//                    return 1;
//                else return 0;
//
//            }
//        });
        if (list.size() == 0) {
            empty_view.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            empty_view.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        
        mAdapter.setItemList(list);
        mAdapter.notifyDataSetChanged();
    }

    private void fetchPostsByTpe(final String type) {
        if (PostsDataHolder.getInstance().getUserPostsCount() > 0) {
            //Posts have alreayd been fetched
            setPostList(type);

        } else {
            showProgressDialog(null, null);
            fetchPosts(type);

        }

    }

    private void fetchPosts(String type) {
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        if (accnt == null)
            return;
        BackendServer.getInstance().fetchPostsForUser(accnt.getEmail(), new ResponseReceiver() {
            @Override
            public void onSuccess(int code, Object result) {
                cancelProgressDialog();
                setPostList(type);
            }

            @Override
            public void onError(String error) {
                cancelProgressDialog();
                showErrorAlert(null, error);

            }
        });

    }

  /*  @Override
    public void onResume() {
        super.onResume();

    }*/

}
