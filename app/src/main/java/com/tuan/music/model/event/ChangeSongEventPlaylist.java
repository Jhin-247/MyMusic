package com.tuan.music.model.event;

import com.tuan.music.model.Song;

public class ChangeSongEventPlaylist {
    private int song;

    public ChangeSongEventPlaylist(int song) {
        this.song = song;
    }

    public int getSong() {
        return song;
    }

    public void setSong(int song) {
        this.song = song;
    }
}
