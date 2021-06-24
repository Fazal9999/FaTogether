package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kptech.peps.Notification.Token;
import com.kptech.peps.R;
import com.kptech.peps.model.Chatlist;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationActivity extends AppBaseActivity  {
    String post_type = "", userid;
    Intent intent;
    private String mCurrentSelType = null;

    private static final String HOME_TYPE = "homeFeed";
    private static final String WORLD_WIDE_TYPE = "worldwideFeed";
    private static final String NEWS_TYPE = "newsAccountFeed";
    private static final String CONTENT_TYPE = "contentFeed";
    private static final String PODCAST_TYPE = "podcastFeed";

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<UserAccount> mUsers;
    private List<Chatlist> usersList;

    TextView empty_view;
    SwipeRefreshLayout swipeRefreshLayout;

    private UserAccount userAccount;

    DatabaseReference reference;
    boolean tab3, tab4, tab5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setHeaderView("Notifications");
        checkIfSessionExpired();

        intent = getIntent();
        post_type = intent.getStringExtra("post_type");

        initTabs();

        initUI();
    }

    private void initTabs() {
        empty_view = findViewById(R.id.empty_view);

        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        userid = accnt.getEmail().replaceAll("[-+.^:,@]","");

        recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration myDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        myDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.cutm_dvdr)));
        recyclerView.addItemDecoration(myDivider);

        usersList = new ArrayList<>();

        TabLayout tabLayout = findViewById(R.id.group_tabs);
        mCurrentSelType = post_type;

        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
//                        if (post_type.equalsIgnoreCase(HOME_TYPE)){
//                            tabLayout.getTabAt(0).select();
//                        }else if (post_type.equalsIgnoreCase(WORLD_WIDE_TYPE)){
//                            tabLayout.getTabAt(1).select();
//                        }else if (post_type.equalsIgnoreCase(NEWS_TYPE)){
//                            tabLayout.getTabAt(2).select();
//                        }else if (post_type.equalsIgnoreCase(CONTENT_TYPE)){
//                            tabLayout.getTabAt(3).select();
//                        }else if (post_type.equalsIgnoreCase(PODCAST_TYPE)){
//                            tabLayout.getTabAt(4).select();
//                        }

                        fetchdData(HOME_TYPE);

                    }
                }, 100);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab3){
                    if (tab4){
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                fetchdData(HOME_TYPE);
                            } else if (tab.getPosition() == 1) {
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);
                            } else if (tab.getPosition() == 2) {
                                mCurrentSelType = NEWS_TYPE;
                                fetchdData(NEWS_TYPE);
                            } else if (tab.getPosition() == 3) {
                                mCurrentSelType = CONTENT_TYPE;
                                fetchdData(CONTENT_TYPE);
                            } else if (tab.getPosition() == 4) {
                                mCurrentSelType = PODCAST_TYPE;
                                fetchdData(PODCAST_TYPE);
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                fetchdData(HOME_TYPE);
                            } else if (tab.getPosition() == 1) {
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);
                            } else if (tab.getPosition() == 2) {
                                mCurrentSelType = NEWS_TYPE;
                                fetchdData(NEWS_TYPE);
                            } else if (tab.getPosition() == 3) {
                                mCurrentSelType = CONTENT_TYPE;
                                fetchdData(CONTENT_TYPE);
                            }
                        }
                    }else{
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                fetchdData(HOME_TYPE);
                            } else if (tab.getPosition() == 1) {
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);
                            } else if (tab.getPosition() == 2) {
                                mCurrentSelType = NEWS_TYPE;
                                fetchdData(NEWS_TYPE);
                            } else if (tab.getPosition() == 3) {
                                mCurrentSelType = PODCAST_TYPE;
                                fetchdData(PODCAST_TYPE);
                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                fetchdData(HOME_TYPE);
                            } else if (tab.getPosition() == 1) {
                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);
                            } else if (tab.getPosition() == 2) {
                                mCurrentSelType = NEWS_TYPE;
                                fetchdData(NEWS_TYPE);
                            }
                        }
                    }
                }else{
                    if (tab4){
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;
                                fetchdData(HOME_TYPE);
                            } else if (tab.getPosition() == 1) {

                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);

                            } else if (tab.getPosition() == 2) {

                                mCurrentSelType = CONTENT_TYPE;
                                fetchdData(CONTENT_TYPE);

                            } else if (tab.getPosition() == 3) {


                                mCurrentSelType = PODCAST_TYPE;
                                fetchdData(PODCAST_TYPE);

                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;

                                fetchdData(HOME_TYPE);

                            } else if (tab.getPosition() == 1) {

                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);

                            } else if (tab.getPosition() == 2) {


                                mCurrentSelType = CONTENT_TYPE;
                                fetchdData(CONTENT_TYPE);

                            } else if (tab.getPosition() == 3) {

                            }
                        }
                    }else{
                        if (tab5){
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;

                                fetchdData(HOME_TYPE);

                            } else if (tab.getPosition() == 1) {

                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);

                            } else if (tab.getPosition() == 2) {

                                mCurrentSelType = PODCAST_TYPE;
                                fetchdData(PODCAST_TYPE);

                            }
                        }else{
                            if (tab.getPosition() == 0) {
                                mCurrentSelType = HOME_TYPE;

                                fetchdData(HOME_TYPE);

                            } else if (tab.getPosition() == 1) {

                                mCurrentSelType = WORLD_WIDE_TYPE;
                                fetchdData(WORLD_WIDE_TYPE);

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


//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if(tab.getPosition() == 0){
//                    mCurrentSelType = HOME_TYPE;
//                    fetchdData(HOME_TYPE);
//                }else if(tab.getPosition() == 1) {
//                    mCurrentSelType = WORLD_WIDE_TYPE;
//                    fetchdData(WORLD_WIDE_TYPE);
//                }else if(tab.getPosition() == 2) {
//                    mCurrentSelType = NEWS_TYPE;
//                    fetchdData(NEWS_TYPE);
//                }else if(tab.getPosition() == 3) {
//                    mCurrentSelType = CONTENT_TYPE;
//                    fetchdData(CONTENT_TYPE);
//                }else if(tab.getPosition() == 4) {
//                    mCurrentSelType = PODCAST_TYPE;
//                    fetchdData(PODCAST_TYPE);
//                }
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


    }

    void initUI() {

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchdData(mCurrentSelType);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

    }

    private void fetchdData(String type){
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(type).child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList(type);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void chatList(String type) {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("USERS");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserAccount user= snapshot.getValue(UserAccount.class);
                    for(Chatlist chatlist : usersList) {
                        if(user.getEmail().replaceAll("[-+.^:,@]","").equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }

                if(mUsers.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.VISIBLE);
                    empty_view.setText("There is no any notifications.");

                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_view.setVisibility(View.GONE);
                }
                adapter = new NotificationAdapter(NotificationActivity.this, mUsers, true, type);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //add for Notification
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(userid).setValue(token1);
    }
}
