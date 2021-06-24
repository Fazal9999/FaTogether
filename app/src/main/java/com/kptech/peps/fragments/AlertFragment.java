package com.kptech.peps.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.kptech.peps.model.Alert;
import com.kptech.peps.model.C2GUser;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.AlertAdapter;
import com.kptech.peps.recycler.Come2GetherAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlertFragment extends AppBaseFragment {

    private RecyclerView mRecycleView;
    private TextView empty_view;
    private AlertAdapter mAdapter;
    List<Alert> alertList = new ArrayList();
    UserAccount userAcct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_alert, container, false);
        userAcct = DataHolder.getInstance().getmCurrentUser();
        initViews(layout);
        return layout;
    }

    private void initViews(View layout) {
        mRecycleView = layout.findViewById(R.id.fragment_alert_list);
        empty_view = layout.findViewById(R.id.empty_alert);
        LinearLayoutManager listManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(listManager);
        mAdapter = new AlertAdapter();
        mAdapter.setContext(getActivity());
        mRecycleView.setAdapter(mAdapter);
        fetchUserAlerts();
    }

    public void fetchUserAlerts() {
        showProgressDialog(null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseConstants.ALERTS).child(Utils.getKey(userAcct.getEmail())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    alertList.clear();

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Alert alert = childDataSnapshot.getValue(Alert.class);
                        alertList.add(alert);
                    }
                    if (alertList.size() == 0) {
                        empty_view.setVisibility(View.VISIBLE);
                        mRecycleView.setVisibility(View.GONE);
                    } else {
                        empty_view.setVisibility(View.GONE);
                        mRecycleView.setVisibility(View.VISIBLE);
                    }

                    Collections.reverse(alertList);

                    mAdapter.setAlertList(alertList);
                    mAdapter.notifyDataSetChanged();
                    cancelProgressDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }
}
