package com.tuan.music.listener;

import com.tuan.music.model.Playlist;

public interface PlaylistListener {
    void onPlaylistClick(int position);
    void onPlaylistLongClickListener(int position);
}
