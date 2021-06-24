package com.kptech.peps.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kptech.peps.R;
import com.kptech.peps.model.Music;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicAdapter extends RecyclerView.Adapter<com.kptech.peps.recycler.MusicAdapter.MyMusicViewHolder> implements View.OnCreateContextMenuListener  {

        private Context mContext;
        private List<Music> musicData = new ArrayList<>();

        public void setMusicList(List<Music> music) {
            if (music != null) {
                musicData = music;
            } else {
                musicData = new ArrayList<>();
            }
        }

        @NonNull
        @Override
        public com.kptech.peps.recycler.MusicAdapter.MyMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_cell, parent, false);
            return new com.kptech.peps.recycler.MusicAdapter.MyMusicViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull com.kptech.peps.recycler.MusicAdapter.MyMusicViewHolder holder, int position) {
            Music music = musicData.get(position);

            if (music != null) {
                if (music.getSongArtwork() != null && !music.getSongArtwork().equals("")) {
                    holder.songArtwork.setScaleType(ImageView.ScaleType.CENTER);
                    Picasso.with(mContext).load(music.getSongArtwork()).error(R.drawable.ic_music_default).into(holder.songArtwork);
                } else {
                    holder.songArtwork.setImageResource(R.drawable.ic_music_default);
                }

                holder.songArtist.setText(music.getSongArtist());
                holder.songName.setText(music.getSongName());
            }
        }

        @Override
        public int getItemCount() {
            if (musicData != null) {
                return musicData.size();
            } else {
                return 0;
            }
        }

        public void setContext(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }

        class MyMusicViewHolder extends RecyclerView.ViewHolder {

            public ImageView songArtwork;
            public CircleImageView playPauseButton;
            public TextView songArtist, songName, songTime;

            @SuppressLint("ResourceAsColor")
            public MyMusicViewHolder(@NonNull View itemView) {
                super(itemView);
                songArtwork = itemView.findViewById(R.id.song_artwork);
                songArtist = itemView.findViewById(R.id.artist_name);
                songName = itemView.findViewById(R.id.song_name);
                songArtwork.setBackgroundColor(R.color.colorPrimary);
                songTime = itemView.findViewById(R.id.music_time);
                playPauseButton = itemView.findViewById(R.id.play_pause_button);
            }
        }
    }
