package com.asuresh.spotifyplaylistcompiler.model;

public class Track {
    private String id;
    private String name;
    private boolean likedSong;

    public Track(String id, String name, boolean likedSong) {
        this.id = id;
        this.name = name;
        this.likedSong = likedSong;
    }

    public Track(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isLikedSong() {
        return likedSong;
    }

    public void setLikedSong(boolean likedSong) {
        this.likedSong = likedSong;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
