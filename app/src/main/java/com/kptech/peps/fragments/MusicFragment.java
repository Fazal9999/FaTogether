package com.kptech.peps.fragments;

import android.os.Bundle;
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
import com.kptech.peps.model.Music;
import com.kptech.peps.recycler.MusicAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends AppBaseFragment {

    private RecyclerView mRecycleView;
    private TextView empty_view;
    private MusicAdapter mAdapter;
    List<Music> musicList = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_music, container, false);
        initViews(layout);
        return layout;
    }

    private void initViews(View layout) {
        mRecycleView = layout.findViewById(R.id.fragment_music_list);
        empty_view = layout.findViewById(R.id.empty_music);
        LinearLayoutManager listManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(listManager);
        mAdapter = new MusicAdapter();
        mAdapter.setContext(getActivity());
        mRecycleView.setAdapter(mAdapter);
        fetchMusic();
    }

    public void fetchMusic() {
        showProgressDialog(null, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(FirebaseConstants.MUSIC).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musicList.clear();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Music music = childDataSnapshot.getValue(Music.class);
                    musicList.add(music);
                }

                if (musicList.size() == 0) {
                    empty_view.setVisibility(View.VISIBLE);
                    mRecycleView.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.GONE);
                    mRecycleView.setVisibility(View.VISIBLE);
                }

                mAdapter.setMusicList(musicList);
                mAdapter.notifyDataSetChanged();
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
