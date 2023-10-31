package com.asuresh.spotifyplaylistcompiler.model;
import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    @SerializedName("display_name")
    private String displayName;
    private boolean albumsPulled;
    private boolean playlistsPulled;
    private boolean tracksPulled;

    public User() {}

    public boolean isAlbumsPulled() {
        return albumsPulled;
    }

    public void setAlbumsPulled(boolean albumsPulled) {
        this.albumsPulled = albumsPulled;
    }

    public boolean isPlaylistsPulled() {
        return playlistsPulled;
    }

    public void setPlaylistsPulled(boolean playlistsPulled) {
        this.playlistsPulled = playlistsPulled;
    }

    public boolean isTracksPulled() {
        return tracksPulled;
    }

    public void setTracksPulled(boolean tracksPulled) {
        this.tracksPulled = tracksPulled;
    }

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
