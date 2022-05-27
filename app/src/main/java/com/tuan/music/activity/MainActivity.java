package com.tuan.music.activity;

import static com.tuan.music.Constants.INTENT_CONSTANT.CAN_PLAY;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
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
import com.tuan.music.listener.Playable;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.PauseOrPlaySongFromNotificationEvent;
import com.tuan.music.model.event.PlaySongHomeEvent;
import com.tuan.music.notification.MyNotificationBuilder;
import com.tuan.music.player.MyPlayer;
import com.tuan.music.services.OnClearFromRecentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeSongListener, Playable {
    ActivityMainBinding binding;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action");
            switch (action) {
                case MyNotificationBuilder.ACTION_NEXT:
                    onPlayNext();
                    break;
                case MyNotificationBuilder.ACTION_PREVIOUS:
                    onPlayPrevious();
                    break;
                case MyNotificationBuilder.ACTION_PLAY:
                    if (MyPlayer.getInstance().isPlaying()) {
                        onPauseSong();
                    } else {
                        onPlay();
                    }
                    break;
            }

        }
    };
    private List<Song> allSongFromDevice;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        checkPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACK_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
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
            MyPlayer.getInstance().setCurrentSongIndex(0);
            Intent intent = new Intent(this, PlayMusicActivity.class);
            intent.putExtra(CAN_PLAY, false);
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

    private void openBottomPlayer() {
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
                onPlay();
                ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_pause);
            } else {
                onPauseSong();
                ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_play);
            }
        });

        binding.layoutPlayMusic.ivPlayNext.setOnClickListener(v -> {
            onPlayNext();
        });

        binding.layoutPlayMusic.ivPlayPrev.setOnClickListener(v -> {
            onPlayPrevious();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaySong(PlaySongHomeEvent event) {
        openBottomPlayer();
    }

    @Override
    public void onSongClick(int song) {
        MyPlayer.getInstance().setCurrentSongIndex(song);
        Intent intent = new Intent(this, PlayMusicActivity.class);
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
        startActivity(intent);
    }

    @Override
    public void onMoreClick(int song) {

    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    MyNotificationBuilder.CHANNEL_ID,
                    "ME",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public void onPlayNext() {
        MyPlayer.getInstance().playNext();
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
    }

    @Override
    public void onPlay() {
        MyPlayer.getInstance().resume();
        EventBus.getDefault().post(new PauseOrPlaySongFromNotificationEvent(true));
        ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_pause);
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
    }

    @Override
    public void onPauseSong() {
        MyPlayer.getInstance().stopMusicWithoutClear();
        EventBus.getDefault().post(new PauseOrPlaySongFromNotificationEvent(true));
        ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_play);
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_play);
    }

    @Override
    public void onPlayPrevious() {
        MyPlayer.getInstance().playPrev();
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
    }

}