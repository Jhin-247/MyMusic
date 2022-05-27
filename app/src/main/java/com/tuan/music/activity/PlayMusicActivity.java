package com.tuan.music.activity;

import static com.tuan.music.Constants.INTENT_CONSTANT.CAN_PLAY;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tuan.music.R;
import com.tuan.music.adapter.PlayMusicPagerAdapter;
import com.tuan.music.databinding.ActivityPlayMusicBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.ChangeSongEventCurrentSong;
import com.tuan.music.model.event.ChangeSongEventPlaylist;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        if (intent.hasExtra(CAN_PLAY)) {
            playFirstTime = intent.getBooleanExtra(CAN_PLAY, true);
        }

        initData();
        initListener();
        initSongAndPlay();
    }

    private void initSongAndPlay() {
        MyPlayer.getInstance().stopMusicAndRelease();
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
        MyPlayer.getInstance().play();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.layoutPlayMusic.skTime.setProgress(MyPlayer.getInstance().getCurrentTime());
                binding.layoutPlayMusic.tvCurrentTime.setText(milliSecondsToTimer(MyPlayer.getInstance().getCurrentTime()));
                binding.layoutPlayMusic.tvTotalTime.setText(milliSecondsToTimer(MyPlayer.getInstance().getCurrentSongDuration()));
                handler.postDelayed(this, 100);
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
        });

        binding.layoutPlayMusic.ivPlayPrev.setOnClickListener(v -> {
            MyPlayer.getInstance().playPrev();
            changeSong();
        });
    }

    private void initData() {
        List<Song> playlist = MyPlayer.getInstance().getCurrentPlaylist();
        Song currentSong = MyPlayer.getInstance().getCurrentSong();
        PlayMusicPagerAdapter adapter = new PlayMusicPagerAdapter(this, playlist, currentSong);
        binding.viewpager.setAdapter(adapter);

        initSongTitle();
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
