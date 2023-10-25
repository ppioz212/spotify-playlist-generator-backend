package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Album;

import java.util.List;

public interface AlbumDao {

    void createAlbum(Album album, String userId);
    void linkTrackToAlbum(String albumId, String trackId);
    List<Album> getAlbums();
}
