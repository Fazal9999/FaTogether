package com.kptech.peps.fragments;

import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.model.C2GUser;
import com.kptech.peps.recycler.Come2GetherAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComeGetherFragment extends AppBaseFragment {

    private Come2GetherAdapter mAdapter;
    List<C2GUser> ctgUserList = new ArrayList();
    List<C2GUser> filteredAgeList = new ArrayList();
    List<C2GUser> filteredGenderList = new ArrayList();
    List<C2GUser> filteredInterestList = new ArrayList();

    public ComeGetherFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_come_gether, container, false);
        initViews(layout);
        return layout;
    }

    private void initViews(View layout) {
        RecyclerView mRecycleView = layout.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecycleView.setLayoutManager(gridLayoutManager);
        mAdapter = new Come2GetherAdapter();
        mAdapter.setContext(getActivity());
        mRecycleView.setAdapter(mAdapter);
        fetchPostsForUser();
    }

    public void fetchPostsForUser() {
        ctgUserList.clear();
        showProgressDialog(null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseConstants.COME2GETHER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    C2GUser data = snapshot.getValue(C2GUser.class);
                    if (data != null) {
                        ctgUserList.add(data);
                    }
                }
                filterUserList(ctgUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void filterUserList(List<C2GUser> users) {
        filteredAgeList.clear();
        String ageRange = Utils.preferences.getString("filterC2GAge", "showAll");

        if (ageRange != null) {
            switch (ageRange) {
                case "18–30":
                    for(C2GUser user : users) {
                        if (user.getCtgAge() < 30) {
                            filteredAgeList.add(user);
                        }
                    }
                case "31–40":
                    for(C2GUser user : users) {
                        if (user.getCtgAge() > 30 && user.getCtgAge() < 41) {
                            filteredAgeList.add(user);
                        }
                    }
                case "41–50":
                    for(C2GUser user : users) {
                        if (user.getCtgAge() > 40 && user.getCtgAge() < 51) {
                            filteredAgeList.add(user);
                        }
                    }
                case "51+":
                    for(C2GUser user : users) {
                        if (user.getCtgAge() > 50) {
                            filteredAgeList.add(user);
                        }
                    }
                default:
                    break;
            }
        }

        if (filteredAgeList.size() > 0) {
            filterGenderList(filteredAgeList);
        } else {
            filterGenderList(users);
        }
    }

    private void filterGenderList(List<C2GUser> users) {
        filteredGenderList.clear();
        String genderFilter = Utils.preferences.getString("filterC2GGender", "showAll");

        if (genderFilter != null) {
            switch (genderFilter) {
                case "Male":
                    for(C2GUser user : users) {
                        if (user.getCtgGender().equals("Male")) {
                            filteredGenderList.add(user);
                        }
                    }
                case "Female":
                    for(C2GUser user : users) {
                        if (user.getCtgGender().equals("Female")) {
                            filteredGenderList.add(user);
                        }
                    }
                case "Other":
                    for(C2GUser user : users) {
                        if (!user.getCtgGender().equals("Male") || !user.getCtgGender().equals("Female")) {
                            filteredGenderList.add(user);
                        }
                    }
                case "Male/Female":
                    for(C2GUser user : users) {
                        if (user.getCtgGender().equals("Male") || user.getCtgGender().equals("Female")) {
                            filteredGenderList.add(user);
                        }
                    }
                case "Male/Other":
                    for(C2GUser user : users) {
                        if (user.getCtgGender().equals("Male") || user.getCtgGender().equals("Other")) {
                            filteredGenderList.add(user);
                        }
                    }
                case "Female/Other":
                    for(C2GUser user : users) {
                        if (user.getCtgGender().equals("Female") || user.getCtgGender().equals("Other")) {
                            filteredGenderList.add(user);
                        }
                    }
                default:
                    break;
            }
        }

        if (filteredGenderList.size() > 0) {
            filterInterestList(filteredGenderList);
        } else {
            filterInterestList(users);
        }
    }

    private void filterInterestList(List<C2GUser> users) {
        filteredInterestList.clear();
        String interestFilter = Utils.preferences.getString("filterC2GInterest", "showAll");

        if (interestFilter != null) {
            switch (interestFilter) {
                case "Casual":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Casual")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Dating":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Dating")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "MeetUps":
                    for(C2GUser user : users) {
                        if (!user.getCtgHereFor().equals("MeetUps")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Chatting":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Chatting")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/Dating":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Casual") || user.getCtgHereFor().equals("Dating")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/MeetUps":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Casual") || user.getCtgHereFor().equals("MeetUps")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/Chatting":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Casual") || user.getCtgHereFor().equals("Chatting")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/Dating/MeetUps":
                    for(C2GUser user : users) {
                        if (!user.getCtgHereFor().equals("Chatting")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/MeetUps/Chatting":
                    for(C2GUser user : users) {
                        if (!user.getCtgHereFor().equals("Dating")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Casual/Dating/Chatting":
                    for(C2GUser user : users) {
                        if (!user.getCtgHereFor().equals("MeetUps")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Dating/MeetUps/Chatting":
                    for(C2GUser user : users) {
                        if (!user.getCtgHereFor().equals("Casual")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Dating/MeetUps":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Dating") || user.getCtgHereFor().equals("MeetUps")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "Dating/Chatting":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("Dating") || user.getCtgHereFor().equals("Chatting")) {
                            filteredInterestList.add(user);
                        }
                    }
                case "MeetUps/Chatting":
                    for(C2GUser user : users) {
                        if (user.getCtgHereFor().equals("MeetUps") || user.getCtgHereFor().equals("Chatting")) {
                            filteredInterestList.add(user);
                        }
                    }
                default:
                    break;
            }

        }

        if (filteredInterestList.size() > 0) {
            mAdapter.setUserList(filteredInterestList);
        } else {
            mAdapter.setUserList(users);
        }

        mAdapter.notifyDataSetChanged();
        cancelProgressDialog();
    }
}
