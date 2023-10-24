package com.asuresh.spotifyplaylistcompiler.model;

public class Track {
    private String id;
    private String name;
    private String userId;
    private boolean likedSong;
    public Track() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isLikedSong() {
        return likedSong;
    }

    public void setLikedSong(boolean likedSong) {
        this.likedSong = likedSong;
    }
}