package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.AlbumDao;
import com.asuresh.spotifyplaylistcompiler.model.Album;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcAlbumDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAlbumDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
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

    public List<Album> getAlbums() {
        List<Album> albums = new ArrayList<>();
        String sql = "SELECT * FROM album;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            albums.add(mapRowToAlbum(results));
        }
        return albums;
    }

    private Album mapRowToAlbum(SqlRowSet results) {
        Album album = new Album();
        album.setUserId(results.getString("id"));
        album.setName(results.getString("name"));
        album.setArtists(results.getString("artists"));
        return album;
    }

    public void linkTrackToAlbum(String albumId, String trackId) {
        String sql = "INSERT INTO album_track (album_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, albumId, trackId);
    }
}
