package com.tuan.music.player;

import android.media.MediaPlayer;

import com.blankj.utilcode.util.ToastUtils;
import com.tuan.music.model.Song;
import com.tuan.music.model.event.PlayNextSongEvent;
import com.tuan.music.model.event.PlaySongHomeEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;


public class MyPlayer {
    private static MyPlayer instance;
    private final MediaPlayer player;

    private List<Song> currentPlaylist;
//    private Song currentSong;
    private int currentSongIndex;

    private boolean canPlayNext, canPlayPrevious;

    private MyPlayer() {
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNext();
            }
        });

    }

    public static MyPlayer getInstance() {
        if (instance == null) {
            instance = new MyPlayer();
        }
        return instance;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public List<Song> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(List<Song> songs) {
        stopMusicAndRelease();
        this.currentPlaylist = songs;
        if (songs.size() > 0) {
            canPlayPrevious = false;
            canPlayNext = songs.size() > 1;
            currentSongIndex = 0;
        }
    }

    public void playNext() {
        if (canPlayNext) {
            currentSongIndex++;
            canPlayPrevious = true;
            canPlayNext = currentSongIndex < currentPlaylist.size() - 1;
            stopMusicAndRelease();
            play();
            EventBus.getDefault().post(new PlayNextSongEvent(currentSongIndex));
        }
    }

    public void playPrev() {
        if (canPlayPrevious) {
            currentSongIndex--;
            canPlayNext = true;
            canPlayPrevious = currentSongIndex > 0;
            stopMusicAndRelease();
            play();
        }
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public int getCurrentSongDuration() {
        return Integer.parseInt(currentPlaylist.get(currentSongIndex).getDuration());
    }

    public Song getCurrentSong() {
        return currentPlaylist.get(currentSongIndex);
    }

    public void setCurrentSong(Song song) {
        stopMusicAndRelease();
        currentSongIndex = currentPlaylist.indexOf(song);
    }

    public void setCurrentSongIndex(int index){
        stopMusicAndRelease();
        currentSongIndex = index;
    }

    public void seekTo(int duration) {
        player.seekTo(duration);
    }

    public int getCurrentTime() {
        return player.getCurrentPosition();
    }

    public void play() {

        try {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
            player.setDataSource(currentPlaylist.get(currentSongIndex).getPath());
            player.prepare();
            player.start();
            EventBus.getDefault().post(new PlaySongHomeEvent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMusicAndRelease() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();

    }

    public void stopMusicWithoutClear() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    public void playFromIndex(Song song) {
        try {
            if (player.isPlaying() && song != currentPlaylist.get(currentSongIndex)) {
                stopMusicAndRelease();
            } else if (!player.isPlaying() && song == currentPlaylist.get(currentSongIndex)) {
                resume();
            }
            setCurrentSong(song);
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            if (!player.isPlaying()) {
                player.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
