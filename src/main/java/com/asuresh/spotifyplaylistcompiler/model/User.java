package com.asuresh.spotifyplaylistcompiler.model;
import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    @SerializedName("display_name")
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
