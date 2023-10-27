package com.asuresh.spotifyplaylistcompiler.services;

import java.util.List;

public class NewPlaylistDTO {
    private List<String> playlistsToAdd;
    private List<String> albumsToAdd;

    private String nameOfPlaylist;
    private boolean addLikedSongs;

    public List<String> getAlbumsToAdd() {
        return albumsToAdd;
    }

    public List<String> getPlaylistsToAdd() {
        return playlistsToAdd;
    }

    public boolean isAddLikedSongs() {
        return addLikedSongs;
    }

    public String getNameOfPlaylist() {
        return nameOfPlaylist;
    }

}
