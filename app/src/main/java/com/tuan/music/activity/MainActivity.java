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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.Constants;
import com.tuan.music.R;
import com.tuan.music.adapter.PlaylistAdapter;
import com.tuan.music.adapter.SongsAdapter;
import com.tuan.music.adapter.searchadapters.PlaylistSearchAdapter;
import com.tuan.music.adapter.searchadapters.SongSearchAdapter;
import com.tuan.music.databinding.ActivityMainBinding;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.dialog.LoadingDialog;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.helper.MusicHelper;
import com.tuan.music.helper.PermissionHelper;
import com.tuan.music.listener.HomeSongListener;
import com.tuan.music.listener.Playable;
import com.tuan.music.listener.PlaylistListener;
import com.tuan.music.listener.SearchListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.ChangeSongEventToMainActivity;
import com.tuan.music.model.event.PauseOrPlaySongFromNotificationEvent;
import com.tuan.music.model.event.PlaySongHomeEvent;
import com.tuan.music.notification.MyNotificationBuilder;
import com.tuan.music.player.MyPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeSongListener, Playable, PlaylistListener {
    ActivityMainBinding binding;
    SQLiteHelper sqLiteHelper;
    LoadingDialog loadingDialog;
    PlaylistAdapter playlistAdapter;
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
    private NotificationManager notificationManager;
    private String keyOld, keySearch;

    private ConcatAdapter concatAdapter;
    private SongSearchAdapter songSearchAdapter;
    private PlaylistSearchAdapter playlistSearchAdapter;
    private List<Song> songSearch;
    private List<Playlist> playlistSearch;
    List<Song> allSongFromDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLiteHelper = new SQLiteHelper(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent, this.getTheme()));
        }
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        checkPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACK_TRACKS"));
//            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
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
        binding.ivSync.setOnClickListener(v -> {
            loadingDialog = new LoadingDialog(this, false);
            loadingDialog.show();
            List<Song> songs = MusicHelper.getAllMusicFromDevice(this);
            for (Song song : songs) {
                sqLiteHelper.insertSong(song);
                if (songs.indexOf(song) == songs.size() - 1) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> loadingDialog.cancel(), 2000);
                }
            }
        });
        binding.btnMyMusic.setOnClickListener(v -> {
            MyPlayer.getInstance().setCurrentPlaylist(allSongFromDB);
            MyPlayer.getInstance().setCurrentSongIndex(0);
            Intent intent = new Intent(this, PlayMusicActivity.class);
            intent.putExtra(CAN_PLAY, false);
            startActivity(intent);
            overridePendingTransition(R.anim.anime_enter, R.anim.anim_exit);
        });

        binding.layoutPlayMusic.ivSongThumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayMusicActivity.class);
            intent.putExtra(CAN_PLAY, MyPlayer.getInstance().isPlaying());
            startActivity(intent);
            overridePendingTransition(R.anim.anime_enter, R.anim.anim_exit);
        });

        binding.btnPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaylistActivity.class);
            startActivity(intent);
        });

        binding.layoutPlaylistNext.setOnClickListener(v -> {
            binding.btnPlaylist.performClick();
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keySearch = binding.etSearch.getText().toString();
                if (!keySearch.equals(keyOld)) {
                    keyOld = keySearch;
                    search(keySearch);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void search(String keySearch) {
        LogUtils.d(keySearch);
        if (!keySearch.equals("")) {
            binding.cslLayout.setVisibility(View.GONE);
            binding.rcvSearch.setVisibility(View.VISIBLE);
        } else {
            binding.cslLayout.setVisibility(View.VISIBLE);
            binding.rcvSearch.setVisibility(View.GONE);
        }
        setAdapter(keySearch);
    }



    private void setAdapter(String keySearch) {
        songSearch = sqLiteHelper.findSongByKey(keySearch);
        songSearchAdapter.setHasData(songSearch.size() != 0);
        songSearchAdapter.setDataSearch(songSearch);

        playlistSearch = sqLiteHelper.findPlaylistByKey(keySearch);
        playlistSearchAdapter.setHasData(playlistSearch.size() != 0);
        playlistSearchAdapter.setDataSearch(playlistSearch);
    }


    private void initData() {
        allSongFromDB = MusicHelper.getAllSongFromDB(this);
        if (allSongFromDB.size() == 0) {
            allSongFromDB = MusicHelper.getAllMusicFromDevice(this);
        }
        SongsAdapter adapter = new SongsAdapter(allSongFromDB, 1, this);
        binding.rcvSongs.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcvSongs.setAdapter(adapter);
        if (MyPlayer.getInstance().getCurrentPlaylist() != null && MyPlayer.getInstance().getCurrentPlaylist().size() != 0)
            MyPlayer.getInstance().setCurrentPlaylist(allSongFromDB);

        playlistAdapter = new PlaylistAdapter(sqLiteHelper.getAllPlaylistFromDB(), this);
        playlistAdapter.setType(1);
        binding.rcvPlaylist.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rcvPlaylist.setAdapter(playlistAdapter);


        initSearchData();
    }

    private void initSearchData() {
        binding.rcvSearch.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        songSearchAdapter = new SongSearchAdapter(new SearchListener() {
            @Override
            public void onItemClick(int position) {
                MyPlayer.getInstance().setCurrentPlaylist(allSongFromDB);
                MyPlayer.getInstance().setCurrentSongId(songSearchAdapter.getSongSearch(position));
                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                startActivity(intent);
            }
        });

        playlistSearchAdapter = new PlaylistSearchAdapter(new SearchListener() {
            @Override
            public void onItemClick(int position) {
                Playlist playlist = sqLiteHelper.getPlaylistById(playlistSearchAdapter.getPlaylistId(position));
                MyPlayer.getInstance().setCurrentPlaylist(playlist.getSongs());
                MyPlayer.getInstance().setCurrentPlaylistId(playlist.getId());
                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                startActivity(intent);
            }
        });
        songSearch = new ArrayList<>();
        playlistSearch = new ArrayList<>();


        concatAdapter = new ConcatAdapter(songSearchAdapter,playlistSearchAdapter);
        binding.rcvSearch.setAdapter(concatAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        initData();
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

        binding.layoutPlayMusic.ivPlayNext.setOnClickListener(v -> onPlayNext());

        binding.layoutPlayMusic.ivPlayPrev.setOnClickListener(v -> onPlayPrevious());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaySong(PlaySongHomeEvent event) {
        openBottomPlayer();
    }

    @Override
    public void onSongClick(int song) {
        MyPlayer.getInstance().setCurrentPlaylist(allSongFromDB);
        if (song != MyPlayer.getInstance().getCurrentSongIndex()) {
            MyPlayer.getInstance().setCurrentSongIndex(song);
        }
        Intent intent = new Intent(this, PlayMusicActivity.class);
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
        startActivity(intent);
        overridePendingTransition(R.anim.anime_enter, R.anim.anim_exit);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSongEvent(ChangeSongEventToMainActivity event) {
        openBottomPlayer();
        MyNotificationBuilder.createNotification(this, MyPlayer.getInstance().getCurrentSong(), MyPlayer.getInstance().getCurrentSongIndex(), MyPlayer.getInstance().getCurrentPlaylist().size(), R.drawable.ic_pause);
    }

    @Override
    public void onPlaylistClick(int position) {
        MyPlayer.getInstance().setCurrentPlaylist(playlistAdapter.getPlaylistAtPosition(position).getSongs());
        MyPlayer.getInstance().setCurrentPlaylistId(playlistAdapter.getPlaylistAtPosition(position).getId());
        Intent intent = new Intent(this, PlayMusicActivity.class);
        intent.putExtra(Constants.INTENT_CONSTANT.IS_PLAYLIST, true);
        startActivity(intent);
    }

    @Override
    public void onPlaylistLongClickListener(int position) {

    }
}