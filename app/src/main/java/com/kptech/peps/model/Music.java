package com.kptech.peps.model;

public class Music {

    public String songArtist;
    public String songArtwork;
    public String songName;
    public String songURL;

    public Music() {}

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongArtwork() {
        return songArtwork;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongArtwork(String songArtwork) {
        this.songArtwork = songArtwork;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

}
