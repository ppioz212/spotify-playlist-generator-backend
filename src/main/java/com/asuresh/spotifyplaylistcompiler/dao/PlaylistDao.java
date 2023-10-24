package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Playlist;

import java.util.List;

public interface PlaylistDao {
    void createPlaylist(Playlist playlist);
    void linkTrackToPlaylist(String playlistID, String trackID);
    List<Playlist> getPlaylists();
}
