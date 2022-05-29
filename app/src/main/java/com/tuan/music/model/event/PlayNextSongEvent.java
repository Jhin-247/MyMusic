package com.tuan.music.model.event;

public class PlayNextSongEvent {
    private int position;

    public PlayNextSongEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
