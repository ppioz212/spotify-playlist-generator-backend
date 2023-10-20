package com.asuresh.spotifyplaylistcompiler.jdbcdao;


import com.asuresh.spotifyplaylistcompiler.dao.TrackDao;
import com.asuresh.spotifyplaylistcompiler.model.Track;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcTrackDao implements TrackDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTrackDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createTrack(Track track) {
        String sql = "INSERT INTO track (id, name, liked_song) VALUES (?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ?;";
        jdbcTemplate.update(sql, track.getId(), track.getName(), track.isLikedSong(), track.isLikedSong());
    }

    public void updateTrackFeatures(Track track) {
        String sql = "INSERT INTO track (id, liked_song, tempo, instrumentalness, time_signature) " +
                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ?, tempo = ?, instrumentalness = ?, time_signature = ?;";
        jdbcTemplate.update(sql, track.getId(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature());
    }
}
