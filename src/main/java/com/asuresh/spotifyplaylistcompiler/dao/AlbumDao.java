package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Album;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class AlbumDao {
    private final JdbcTemplate jdbcTemplate;

    public AlbumDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createAlbum(Album album) {
        String sql = "INSERT INTO album (id, name, artists) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET name = ? RETURNING id;";
        jdbcTemplate.queryForObject(sql, String.class, album.getId(), album.getName(), album.getArtists(), album.getName());
    }

// TODO: stupid way of doing, but don't need return object. Ask Walt later for this.
//    public void create_album(Album album) {
//        String sql = String.format("INSERT INTO album (id, name, artists) VALUES ('%s', '%s', '%s');",
//                album.getId(), album.getName(), album.getArtists());
//        jdbcTemplate.execute(sql);
//    }
}
