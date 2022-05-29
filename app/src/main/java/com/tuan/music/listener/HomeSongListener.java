package com.tuan.music.listener;

import com.tuan.music.model.Song;

public interface HomeSongListener {
    void onSongClick(int songPosition);
    void onMoreClick(int songPosition);
}
