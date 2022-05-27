package com.tuan.music.activity;

import static com.tuan.music.Constants.INTENT_CONSTANT.CAN_PLAY;
import static com.tuan.music.Constants.INTENT_CONSTANT.CURRENT_SONG;
import static com.tuan.music.Constants.INTENT_CONSTANT.PLAYLIST;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.R;
import com.tuan.music.adapter.SongsAdapter;
import com.tuan.music.databinding.ActivityMainBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.helper.PermissionHelper;
import com.tuan.music.listener.HomeSongListener;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.PlaySongHomeEvent;
import com.tuan.music.player.MyPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeSongListener {
    ActivityMainBinding binding;
    private List<Song> allSongFromDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        checkPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void checkPermission() {
        if (PermissionHelper.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            initData();
            initListener();
        } else {
            PermissionHelper.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    private void initListener() {
        binding.btnMyMusic.setOnClickListener(v -> {
            MyPlayer.getInstance().setCurrentSong(allSongFromDevice.get(0));
            Intent intent = new Intent(this,PlayMusicActivity.class);
            intent.putExtra(CAN_PLAY,false);
            startActivity(intent);
        });
    }

    private void initData() {
        allSongFromDevice = MusicHelper.getAllMusicFromDevice(this);
        SongsAdapter adapter = new SongsAdapter(allSongFromDevice, 1, this);
        binding.rcvSongs.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcvSongs.setAdapter(adapter);
        MyPlayer.getInstance().setCurrentPlaylist(allSongFromDevice);

    }

    private void openBottomPlayer(){
        binding.layoutPlayMusic.getRoot().setVisibility(View.VISIBLE);
        setupBottomView();
        setupBottomListener();
    }

    private void setupBottomView() {
        binding.layoutPlayMusic.tvSongName.setText(MyPlayer.getInstance().getCurrentSong().getTitle());
        Glide.with(binding.getRoot().getContext()).load(MyPlayer.getInstance().getCurrentSong().getThumbnail()).placeholder(R.drawable.notes).into(binding.layoutPlayMusic.ivSongThumbnail);
        Glide.with(this).load(R.drawable.bg_play_music).centerCrop().into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.layoutPlayMusic.getRoot().setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                binding.layoutPlayMusic.getRoot().setBackground(placeholder);
            }
        });
    }

    private void setupBottomListener() {
        binding.layoutPlayMusic.ivPlay.setOnClickListener(v -> {
            if (!MyPlayer.getInstance().isPlaying()) {
                MyPlayer.getInstance().resume();
                ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_pause);
            } else {
                MyPlayer.getInstance().stopMusicWithoutClear();
                ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_play);
            }
        });

        binding.layoutPlayMusic.ivPlayNext.setOnClickListener(v -> {
            MyPlayer.getInstance().playNext();
        });

        binding.layoutPlayMusic.ivPlayPrev.setOnClickListener(v -> {
            MyPlayer.getInstance().playPrev();
//            changeSong();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaySong(PlaySongHomeEvent event){
        openBottomPlayer();
    }

    @Override
    public void onSongClick(int song) {
        MyPlayer.getInstance().setCurrentSongIndex(song);
        Intent intent = new Intent(this, PlayMusicActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMoreClick(int song) {

    }
}