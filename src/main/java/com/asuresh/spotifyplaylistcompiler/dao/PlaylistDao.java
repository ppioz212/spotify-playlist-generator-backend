package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class PlaylistDao {
    private final JdbcTemplate jdbcTemplate;

    public PlaylistDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlist (id, name, owner) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET name = ? RETURNING id;";
        jdbcTemplate.queryForObject(sql, String.class, playlist.getId(), playlist.getName(), playlist.getOwner(), playlist.getName());
    }
}
