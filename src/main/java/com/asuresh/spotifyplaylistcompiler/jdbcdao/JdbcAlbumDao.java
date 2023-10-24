package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.AlbumDao;
import com.asuresh.spotifyplaylistcompiler.model.Album;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcAlbumDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAlbumDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createAlbum(Album album) {
        String sql = "INSERT INTO album (id, name, artists, user_id) VALUES (?, ?, ?, ?) ON CONFLICT (id) " +
                "DO NOTHING";
        jdbcTemplate.update(sql,
                album.getId(),
                album.getName(),
                album.getArtists(),
                album.getUserId());
    }

    public List<Album> getAlbums() {
        return null;
    }

    public void linkTrackToAlbum(String albumID, String trackID) {
        String sql = "INSERT INTO album_track (album_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, albumID, trackID);
    }
}
