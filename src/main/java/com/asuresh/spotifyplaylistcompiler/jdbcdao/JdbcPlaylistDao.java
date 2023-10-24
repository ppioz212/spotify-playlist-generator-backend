package com.asuresh.spotifyplaylistcompiler.jdbcdao;

import com.asuresh.spotifyplaylistcompiler.dao.PlaylistDao;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import com.asuresh.spotifyplaylistcompiler.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcPlaylistDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlist (id, name, owner, user_id) VALUES (?, ?, ?, ?) ON CONFLICT (id) " +
                "DO NOTHING;";
        jdbcTemplate.update(sql,
                playlist.getId(),
                playlist.getName(),
                playlist.getOwner(),
                playlist.getUserId());
    }

    public void createUser(User user) {
        String sql = "INSERT INTO user_profile (id, display_name) " +
                "VALUES (?, ?) ON CONFLICT (id) DO NOTHING;";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getDisplayName());
    }

    public List<Playlist> getPlaylists() {
        return null;
    }

    public void linkTrackToPlaylist(String playlistID, String trackID) {
        String sql = "INSERT INTO playlist_track (playlist_id, track_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, playlistID, trackID);
    }
}
