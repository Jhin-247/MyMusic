package com.tuan.music.activity;

import static com.tuan.music.Constants.INTENT_CONSTANT.CAN_PLAY;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.Constants;
import com.tuan.music.R;
import com.tuan.music.adapter.PlayMusicPagerAdapter;
import com.tuan.music.databinding.ActivityPlayMusicBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.ChangeSongEventCurrentSong;
import com.tuan.music.model.event.ChangeSongEventPlaylist;
import com.tuan.music.model.event.ChangeSongEventToMainActivity;
import com.tuan.music.model.event.PauseOrPlaySongFromNotificationEvent;
import com.tuan.music.model.event.PlayNextSongEvent;
import com.tuan.music.model.event.PlaySongEvent;
import com.tuan.music.player.MyPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class PlayMusicActivity extends AppCompatActivity {
    ActivityPlayMusicBinding binding;
    private boolean playFirstTime = true;
    private Handler handler;
    private boolean isPlaylist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        ToastUtils.showShort(MyPlayer.getInstance().getCurrentSongIndex());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent, this.getTheme()));
        }
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Intent intent = getIntent();
        if (intent.hasExtra(CAN_PLAY)) {
            playFirstTime = intent.getBooleanExtra(CAN_PLAY, true);
        }

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

        initData();
        initListener();
    }

    private void initSongAndPlay() {
        binding.layoutPlayMusic.skTime.setMax(Integer.parseInt(MyPlayer.getInstance().getCurrentSong().getDuration()));
        binding.layoutPlayMusic.skTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MyPlayer.getInstance().seekTo(progress);
                    binding.layoutPlayMusic.skTime.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (!MyPlayer.getInstance().isPlaying()) {
            MyPlayer.getInstance().stopMusicAndRelease();
            MyPlayer.getInstance().play();
            changeSong();
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.layoutPlayMusic.skTime.setProgress(MyPlayer.getInstance().getCurrentTime());
                binding.layoutPlayMusic.tvCurrentTime.setText(milliSecondsToTimer(MyPlayer.getInstance().getCurrentTime()));
                binding.layoutPlayMusic.tvTotalTime.setText(milliSecondsToTimer(MyPlayer.getInstance().getCurrentSongDuration()));
                handler.postDelayed(this, 1);
            }
        }, 0);
        if (!playFirstTime) {
            ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_play);
            MyPlayer.getInstance().stopMusicWithoutClear();
        }
    }

    private void initListener() {
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
            binding.layoutPlayMusic.skTime.setMax(Integer.parseInt(MyPlayer.getInstance().getCurrentSong().getDuration()));
        });

        binding.layoutPlayMusic.ivPlayPrev.setOnClickListener(v -> {
            MyPlayer.getInstance().playPrev();
            changeSong();
        });
        binding.ivMinimize.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.anim_down, R.anim.anim_down);
        });
        binding.ivMoreOption.setOnClickListener(v -> {

        });
    }

    private void initData() {
        Intent intent = getIntent();
        if(intent.hasExtra(Constants.INTENT_CONSTANT.IS_PLAYLIST)){
            isPlaylist = intent.getBooleanExtra(Constants.INTENT_CONSTANT.IS_PLAYLIST,false);
        }
        binding.ivMoreOption.setVisibility(View.GONE);

        List<Song> playlist = MyPlayer.getInstance().getCurrentPlaylist();
        Song currentSong = MyPlayer.getInstance().getCurrentSong();

        PlayMusicPagerAdapter adapter = new PlayMusicPagerAdapter(this, playlist, currentSong);
        binding.viewpager.setAdapter(adapter);

        initSongTitle();

        initSongAndPlay();

    }

    private void initSongTitle() {

        Song currentSong = MyPlayer.getInstance().getCurrentSong();
        binding.tvSongName.setText(currentSong.getTitle());
        if (!currentSong.getArtist().equals("<unknown>")) {
            binding.tvSongArtist.setText(currentSong.getArtist());
            binding.tvSongArtist.setVisibility(View.VISIBLE);
        } else {
            binding.tvSongArtist.setVisibility(View.GONE);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayNextSongEvent(PlayNextSongEvent event) {
        changeSong();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaySong(PlaySongEvent event) {
        ImageHelper.setImage(this, binding.layoutPlayMusic.ivPlay, R.drawable.ic_pause);
        changeSong();
    }

    private void changeSong() {
        initSongTitle();
        EventBus.getDefault().post(new ChangeSongEventPlaylist(MyPlayer.getInstance().getCurrentSongIndex()));
        EventBus.getDefault().post(new ChangeSongEventCurrentSong(MyPlayer.getInstance().getCurrentSong()));
        EventBus.getDefault().post(new ChangeSongEventToMainActivity());
        binding.layoutPlayMusic.skTime.setMax(Integer.parseInt(MyPlayer.getInstance().getCurrentSong().getDuration()));
    }

    public String milliSecondsToTimer(int milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceChange(PauseOrPlaySongFromNotificationEvent event) {

    }

}
