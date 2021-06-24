package com.kptech.peps.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.model.Conversation;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.LatestMessage;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.RecentMessageAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends AppBaseFragment {

    private TextView empty_view;
    private RecyclerView mRecycleView;
    private RecentMessageAdapter mAdapter;
    List<Conversation> convoList = new ArrayList();
    UserAccount userAcct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_message, container, false);
        userAcct = DataHolder.getInstance().getmCurrentUser();
        initViews(layout);
        return layout;
    }

    private void initViews(View layout) {
        mRecycleView = layout.findViewById(R.id.fragment_message_list);
        empty_view = layout.findViewById(R.id.empty_msgs);
        LinearLayoutManager listManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(listManager);
        mAdapter = new RecentMessageAdapter();
        mAdapter.setContext(getActivity());
        mRecycleView.setAdapter(mAdapter);
        fetchConvos();
    }

    private void fetchConvos(){
        showProgressDialog(null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseConstants.USERS).child(Utils.getKey(userAcct.getEmail())).child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Conversation convo = childSnapshot.getValue(Conversation.class);
                    for(DataSnapshot child: childSnapshot.getChildren()) {
                        if(child.getKey().equals("latestMessage")) {
                           LatestMessage latestMsg = child.getValue(LatestMessage.class);
                            if (convo != null) {
                                convo.setLatest_message(latestMsg);
                                convoList.add(convo);
                            }
                        }
                    }
                }

                if (convoList.size() > 0){
                    empty_view.setVisibility(View.GONE);
                    mRecycleView.setVisibility(View.VISIBLE);
                } else {
                    empty_view.setVisibility(View.VISIBLE);
                }

                mAdapter.setConvoList(convoList);
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
