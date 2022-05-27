package com.tuan.music.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String path;
    private String duration;
    private String title;
    private String artist;
    private String thumbnail;
    private long albumId;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean equals (Song obj) {
        if(!this.artist.equals(obj.getArtist())){
            return false;
        }

        if(this.thumbnail != null){
            if(obj.getThumbnail() == null){
                return false;
            }
        }

        if(obj.thumbnail != null){
            if(this.getThumbnail() == null){
                return false;
            }
        }

        if((obj.getThumbnail() != null && this.thumbnail != null) && !this.thumbnail.equals(obj.getThumbnail())){
            return false;
        }
        if(!this.path.equals(obj.getPath())){
            return false;
        }
        if(!this.duration.equals(obj.getDuration())){
            return false;
        }
        if(!this.title.equals(obj.getTitle())){
            return false;
        }
        return this.albumId == obj.getAlbumId();
    }
}
