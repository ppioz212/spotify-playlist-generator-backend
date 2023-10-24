package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.PlaylistDao;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcPlaylistDao implements PlaylistDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlist (id, name, owner) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO NOTHING;";
        jdbcTemplate.update(sql, playlist.getId(), playlist.getName(), playlist.getOwner());
    }

    @Override
    public List<Playlist> getPlaylists() {
        return null;
    }

    public void linkTrackToPlaylist(String playlistID, String trackID) {
        String sql = "INSERT INTO playlist_track (playlist_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, playlistID, trackID);
    }
}
