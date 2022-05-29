package com.tuan.music.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.Constants;
import com.tuan.music.R;
import com.tuan.music.adapter.SongInPlaylistAdapter;
import com.tuan.music.databinding.ModifyPlaylistActivityBinding;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;

import java.util.List;

public class ModifyPlaylistActivity extends AppCompatActivity implements PlaylistSongAddingClickListener {
    ModifyPlaylistActivityBinding binding;
    SQLiteHelper sqLiteHelper;
    private Playlist playlist;
    private SongInPlaylistAdapter adapter;

    private List<Song> songs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ModifyPlaylistActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent, this.getTheme()));
        }
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Glide.with(this).load(R.drawable.bg_play_music).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.layout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                binding.layout.setBackground(placeholder);
            }
        });
        sqLiteHelper = new SQLiteHelper(this);

        initData();
        initListener();
    }

    private void initListener() {
        binding.btnAddPlaylist.setOnClickListener(v -> {
            if (binding.etPlaylistName.getText().toString().isEmpty()) {
                ToastUtils.showShort("Dien ten");
                return;
            }
            List<Song> songChosen = adapter.getChosenSong();
            if (songChosen.size() == 0) {
                ToastUtils.showShort("Chon bai hat");
                return;
            }

            for (Song song : adapter.getSongs()) {
                if (song.isChosen()) {
                    boolean isInPlaylist = false;
                    for (Song song1 : playlist.getSongs()) {
                        if (song1.getTitle().equals(song.getTitle())) {
                            isInPlaylist = true;
                            break;
                        }
                    }
                    if (!isInPlaylist) {
                        sqLiteHelper.addSongToPlaylist(song.getId(), playlist.getId());
                    }
                } else {
                    boolean isInPlaylist = false;
                    for (Song song1 : playlist.getSongs()) {
                        if (song1.getTitle().equals(song.getTitle())) {
                            isInPlaylist = true;
                            break;
                        }
                    }
                    if (isInPlaylist) {
                        sqLiteHelper.deleteSongFromPlaylist(song.getId(), playlist.getId());
                    }
                }
            }
            finish();
        });
    }

    private void initData() {
        Intent intent = getIntent();
        playlist = sqLiteHelper.getPlaylistById(intent.getIntExtra(Constants.DB_CONSTANTS.PLAYLIST_ID, 0));

        songs = sqLiteHelper.getAllSongs();
        if (songs.size() == 0) {
            songs = MusicHelper.getAllMusicFromDevice(this);
        }

        for (Song song : songs) {
            for (Song song1 : playlist.getSongs()) {
                if (song.getTitle().equals(song1.getTitle())) {
                    song.setChosen(true);
                    break;
                }
            }
        }

        binding.etPlaylistName.setText(playlist.getName());
        binding.rcvSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongInPlaylistAdapter(songs, this);
        binding.rcvSongs.setAdapter(adapter);
    }

    @Override
    public void onSongClick(int position) {
        adapter.changeStatus(position);
    }
}
