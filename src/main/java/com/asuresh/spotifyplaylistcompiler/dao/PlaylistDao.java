package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Playlist;

public interface PlaylistDao {
    void createPlaylist(Playlist playlist);
}
