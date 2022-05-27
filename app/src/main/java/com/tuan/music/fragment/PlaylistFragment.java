package com.tuan.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tuan.music.adapter.SongsAdapter;
import com.tuan.music.databinding.FragmentPlaylistBinding;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.listener.HomeSongListener;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.ChangeSongEventPlaylist;
import com.tuan.music.model.event.PlaySongEvent;
import com.tuan.music.player.MyPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class PlaylistFragment extends Fragment implements HomeSongListener {
    FragmentPlaylistBinding binding;
    private List<Song> playlist;
    private SongsAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);
        initData();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
    }

    private void initData() {
        playlist = MusicHelper.getAllMusicFromDevice(requireContext());
        adapter = new SongsAdapter(playlist, 0, this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex());
        binding.rcvPlaylist.setHasFixedSize(true);
        binding.rcvPlaylist.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcvPlaylist.setAdapter(adapter);
        binding.rcvPlaylist.smoothScrollToPosition(adapter.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSong(ChangeSongEventPlaylist event) {
        adapter.setCurrentSong(event.getSong());
        MyPlayer.getInstance().setCurrentSongIndex(event.getSong());
        MyPlayer.getInstance().play();
        binding.rcvPlaylist.smoothScrollToPosition(adapter.getPosition());
    }

    @Override
    public void onSongClick(int song) {
        adapter.setCurrentSong(song);
        MyPlayer.getInstance().setCurrentSongIndex(song);
        MyPlayer.getInstance().play();
        binding.rcvPlaylist.smoothScrollToPosition(adapter.getPosition());
        EventBus.getDefault().post(new PlaySongEvent());
    }

    @Override
    public void onMoreClick(int song) {

    }
}
