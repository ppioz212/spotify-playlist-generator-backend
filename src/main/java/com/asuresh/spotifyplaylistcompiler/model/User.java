package com.asuresh.spotifyplaylistcompiler.model;

public class User {
    private String id;
    private String displayName;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
