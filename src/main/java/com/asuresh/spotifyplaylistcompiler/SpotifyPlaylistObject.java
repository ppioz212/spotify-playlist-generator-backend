package com.asuresh.spotifyplaylistcompiler;

public class SpotifyPlaylistObject {

    private String playlistId;
    private String playlistName;
    private PlaylistTypeEnum playlistType;
    private String playlistOwner;
    private String imageLink;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public PlaylistTypeEnum getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(PlaylistTypeEnum playlistType) {
        this.playlistType = playlistType;
    }

    public String getPlaylistOwner() {
        return playlistOwner;
    }

    public void setPlaylistOwner(String playlistOwner) {
        this.playlistOwner = playlistOwner;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
