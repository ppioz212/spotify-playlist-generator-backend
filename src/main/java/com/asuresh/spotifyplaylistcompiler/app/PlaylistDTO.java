package com.asuresh.spotifyplaylistcompiler.app;

import java.util.List;

public class PlaylistDTO {
    private List<String> playlistsToAdd;
    private List<String> albumsToAdd;

    private String nameOfPlaylist;
    private boolean addLikedSongs;

    public List<String> getAlbumsToAdd() {
        return albumsToAdd;
    }

    public void setAlbumsToAdd(List<String> albumsToAdd) {
        this.albumsToAdd = albumsToAdd;
    }

    public List<String> getPlaylistsToAdd() {
        return playlistsToAdd;
    }

    public void setPlaylistsToAdd(List<String> playlistsToAdd) {
        this.playlistsToAdd = playlistsToAdd;
    }

    public boolean isAddLikedSongs() {
        return addLikedSongs;
    }

    public void setAddLikedSongs(boolean addLikedSongs) {
        this.addLikedSongs = addLikedSongs;
    }

    public String getNameOfPlaylist() {
        return nameOfPlaylist;
    }

    public void setNameOfPlaylist(String nameOfPlaylist) {
        this.nameOfPlaylist = nameOfPlaylist;
    }
}
