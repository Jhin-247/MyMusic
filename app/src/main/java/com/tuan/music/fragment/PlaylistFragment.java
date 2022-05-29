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
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.listener.HomeSongListener;
import com.tuan.music.model.Playlist;
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
    private SQLiteHelper sqLiteHelper;

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
        sqLiteHelper = new SQLiteHelper(requireContext());
        initData();
        initListener();

        return binding.getRoot();
    }

    private void initListener() {
    }

    private void initData() {
        playlist = MyPlayer.getInstance().getCurrentPlaylist();
        adapter = new SongsAdapter(playlist, 0, this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex());
        binding.rcvPlaylist.setHasFixedSize(true);
        binding.rcvPlaylist.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcvPlaylist.setAdapter(adapter);
        binding.rcvPlaylist.smoothScrollToPosition(adapter.getPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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
        if (song != MyPlayer.getInstance().getCurrentSongIndex()) {
            adapter.setCurrentSong(song);
            MyPlayer.getInstance().setCurrentSongIndex(song);
            MyPlayer.getInstance().play();
            EventBus.getDefault().post(new PlaySongEvent());
        } else {

        }

    }

    @Override
    public void onMoreClick(int song) {
        sqLiteHelper.deleteSongFromPlaylist(adapter.getSongs().get(song).getId(),MyPlayer.getInstance().getPlaylistId());
        Playlist playlist = sqLiteHelper.getPlaylistById(MyPlayer.getInstance().getPlaylistId());
        MyPlayer.getInstance().setCurrentPlaylistWithoutChangingPlaylistId(sqLiteHelper.getPlaylistById(playlist.getId()).getSongs());
        initData();
    }
}
