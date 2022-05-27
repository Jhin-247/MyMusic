package com.tuan.music.model.event;

import com.tuan.music.model.Song;

public class ChangeSongEventCurrentSong {
    private Song song;

    public ChangeSongEventCurrentSong(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
