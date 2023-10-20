package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.AlbumDao;
import com.asuresh.spotifyplaylistcompiler.model.Album;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcAlbumDao implements AlbumDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAlbumDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createAlbum(Album album) {
        String sql = "INSERT INTO album (id, name, artists) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET name = ?";
        jdbcTemplate.update(sql, album.getId(), album.getName(), album.getArtists(), album.getName());
    }

    public void addAlbumToTrack(String albumID, String trackID) {
        String sql = "INSERT INTO album_track (album_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, albumID, trackID);
    }
}