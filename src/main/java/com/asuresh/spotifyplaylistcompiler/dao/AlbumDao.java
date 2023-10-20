package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Album;

import java.util.List;

public interface AlbumDao {

    void createAlbum(Album album);
    void insertTrackToAlbum(String albumID, String trackID);
    List<Album> getAlbums();
}
