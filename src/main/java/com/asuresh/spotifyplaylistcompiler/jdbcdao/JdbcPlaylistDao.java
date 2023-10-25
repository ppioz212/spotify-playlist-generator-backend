package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import com.asuresh.spotifyplaylistcompiler.model.playlistmodel.Owner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcPlaylistDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createPlaylist(Playlist playlist, String userId) {
        String sql = "INSERT INTO playlist (id, name, owner_id, owner_display_name, user_id) VALUES (?, ?, ?, ?, ?) ON CONFLICT (id) " +
                "DO NOTHING;";
        jdbcTemplate.update(sql,
                playlist.getId(),
                playlist.getName(),
                playlist.getOwner().getId(),
                playlist.getOwner().getDisplayName(),
                userId);
    }

    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlist;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            playlists.add(mapRowToPlaylist(results));
        }
        return playlists;
    }

    private Playlist mapRowToPlaylist(SqlRowSet results) {
        Playlist playlist = new Playlist();
        playlist.setName(results.getString("name"));
        playlist.setId(results.getString("id"));
        playlist.setOwner(new Owner(
                results.getString("owner_id"),
                results.getString("owner_display_name")));
        return playlist;
    }

    public void linkTrackToPlaylist(String playlistId, String trackId) {
        String sql = "INSERT INTO playlist_track (playlist_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, playlistId, trackId);
    }
}
