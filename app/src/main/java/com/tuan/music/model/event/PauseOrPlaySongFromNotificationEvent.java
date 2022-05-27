package com.tuan.music.model.event;

public class PauseOrPlaySongFromNotificationEvent {
    private boolean isPlay;

    public PauseOrPlaySongFromNotificationEvent(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
