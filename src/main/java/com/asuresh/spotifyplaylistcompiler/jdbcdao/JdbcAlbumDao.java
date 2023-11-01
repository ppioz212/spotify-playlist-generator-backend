package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.AlbumDao;
import com.asuresh.spotifyplaylistcompiler.model.Album;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAlbumDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAlbumDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createAlbum(Album album, String user_id) {
        String sql = "INSERT INTO album (id, name, artists, user_id) VALUES (?, ?, ?, ?) ON CONFLICT (id) " +
                "DO NOTHING";
        jdbcTemplate.update(sql,
                album.getId(),
                album.getName(),
                album.getArtists(),
                user_id);
    }

    public List<Album> getAlbums(String userId) {
        List<Album> albums = new ArrayList<>();
        String sql = "SELECT * FROM album where user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            albums.add(mapRowToAlbum(results));
        }
        return albums;
    }

    public List<String> getAlbumIds() {
        List<String> albums = new ArrayList<>();
        String sql = "SELECT * FROM album;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            albums.add(results.getString("id"));
        }
        return albums;
    }

    private Album mapRowToAlbum(SqlRowSet results) {
        Album album = new Album();
        album.setId(results.getString("id"));
        album.setUserId(results.getString("user_id"));
        album.setName(results.getString("name"));
        album.setArtists(results.getString("artists"));
        return album;
    }

    public void linkTrackToAlbum(String albumId, String trackId) {
        String sql = "INSERT INTO album_track (album_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, albumId, trackId);
    }

    public void deleteAlbums(String userId) {
        String albumUnlinkSql = "DELETE FROM album_track WHERE " +
                " album_id in (SELECT id FROM album WHERE user_id = ?);";
        String albumSql = "DELETE FROM album WHERE user_id = ?";
        jdbcTemplate.update(albumUnlinkSql, userId);
        jdbcTemplate.update(albumSql, userId);
    }
}
