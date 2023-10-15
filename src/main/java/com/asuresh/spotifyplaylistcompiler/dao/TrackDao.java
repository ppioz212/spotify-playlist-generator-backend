package com.asuresh.spotifyplaylistcompiler.dao;


import com.asuresh.spotifyplaylistcompiler.model.Track;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class TrackDao {
    private final JdbcTemplate jdbcTemplate;

    public TrackDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createTrack(Track track) {
        String sql = "INSERT INTO track (id, name, liked_song) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ? RETURNING id;";
        jdbcTemplate.queryForObject(sql, String.class, track.getId(), track.getName(), track.isLikedSong(), track.isLikedSong());
    }

    public void createTrackExtra(Track track) {
        String sql = "INSERT INTO track (id, liked_song, tempo, instrumentalness, time_signature) " +
                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ?, tempo = ?, instrumentalness = ?, time_signature = ? RETURNING id;";
        jdbcTemplate.queryForObject(sql, String.class, track.getId(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature());
    }
}
