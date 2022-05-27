package com.tuan.music.adapter;

import static com.tuan.music.Constants.INTENT_CONSTANT.CURRENT_SONG;
import static com.tuan.music.Constants.INTENT_CONSTANT.PLAYLIST;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tuan.music.fragment.PlaylistFragment;
import com.tuan.music.fragment.PlayMusicFragment;
import com.tuan.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayMusicPagerAdapter extends FragmentStateAdapter {
    private List<Song> playlist;
    private Song currentSong;


    public PlayMusicPagerAdapter(@NonNull FragmentActivity fragmentActivity,List<Song> playlist,Song currentSong) {
        super(fragmentActivity);
        this.currentSong = currentSong;
        this.playlist = playlist;
    }

    public PlayMusicPagerAdapter(@NonNull Fragment fragment,List<Song> playlist,Song currentSong) {
        super(fragment);
        this.currentSong = currentSong;
        this.playlist = playlist;
    }

    public PlayMusicPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,List<Song> playlist,Song currentSong) {
        super(fragmentManager, lifecycle);
        this.currentSong = currentSong;
        this.playlist = playlist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new PlaylistFragment();
            Bundle bundle = new Bundle();
            ArrayList<Song> songs = new ArrayList<>(playlist.size());
            songs.addAll(playlist);
            bundle.putSerializable(PLAYLIST, songs);
            fragment.setArguments(bundle);
        } else {
            fragment = new PlayMusicFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_SONG, currentSong);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
