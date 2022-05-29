package com.tuan.music.activity;

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
import com.tuan.music.R;
import com.tuan.music.adapter.SongInPlaylistAdapter;
import com.tuan.music.databinding.ActivityAddPlaylistBinding;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;

import java.util.List;

public class AddPlaylistActivity extends AppCompatActivity implements PlaylistSongAddingClickListener {
    ActivityAddPlaylistBinding binding;
    SQLiteHelper sqLiteHelper;
    SongInPlaylistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPlaylistBinding.inflate(getLayoutInflater());
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

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initListener() {
        binding.btnAddPlaylist.setOnClickListener(v -> {
            if(binding.etPlaylistName.getText().toString().equals("")){
                ToastUtils.showShort("Dien du thong tin");
                return;
            }
            if(adapter.getNumberChosen() == 0){
                ToastUtils.showShort("Mooi chon bai hat");
                return;
            }
            Playlist playlist = new Playlist();
            playlist.setImage("");
            playlist.setSongs(adapter.getChosenSong());
            playlist.setName(binding.etPlaylistName.getText().toString());
            int result = sqLiteHelper.insertPlaylist(playlist);
            if(result == -1){
                ToastUtils.showShort("Da co Playlist voi ten nay");
            } else if(result == 1){
                finish();
            }
        });
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        List<Song> songList = sqLiteHelper.getAllSongs();
        if(songList.size() == 0){
            songList = MusicHelper.getAllSongFromDB(this);
        }
        adapter = new SongInPlaylistAdapter(songList,this);
        binding.rcvSongs.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.rcvSongs.setHasFixedSize(true);
        binding.rcvSongs.setAdapter(adapter);

    }

    @Override
    public void onSongClick(int position) {
        adapter.changeStatus(position);
        binding.tvSongNumberChosen.setText("Song chose: " + adapter.getNumberChosen());
    }
}
