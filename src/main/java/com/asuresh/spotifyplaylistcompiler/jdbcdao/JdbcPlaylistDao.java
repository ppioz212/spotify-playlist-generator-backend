package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.PlaylistDao;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcPlaylistDao implements PlaylistDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlist (id, name, owner) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET name = ?;";
        jdbcTemplate.update(sql, playlist.getId(), playlist.getName(), playlist.getOwner(), playlist.getName());
    }
    public void addPlaylistToTrack(String playlistID, String trackID) {
        String sql = "INSERT INTO playlist_track (playlist_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, playlistID, trackID);
    }
}
