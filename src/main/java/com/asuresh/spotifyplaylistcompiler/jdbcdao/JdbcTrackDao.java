package com.asuresh.spotifyplaylistcompiler.jdbcdao;


import com.asuresh.spotifyplaylistcompiler.model.AudioFeature;
import com.asuresh.spotifyplaylistcompiler.model.Track;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTrackDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTrackDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createTrack(Track track) {
        String sql = "INSERT INTO track (id, name, liked_song, user_id) VALUES (?, ?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ?;";
        jdbcTemplate.update(sql,
                track.getId(),
                track.getName(),
                track.isLikedSong(),
                track.getUserId(),
                track.isLikedSong());
    }
    public void createAudioFeatures(AudioFeature audioFeature) {
        String sql = "INSERT INTO track (id, tempo, instrumentalness, time_signature) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET tempo = ?, instrumentalness = ?, time_signature = ?;";
        jdbcTemplate.update(sql, audioFeature.getId(),
                 audioFeature.getTempo(), audioFeature.getInstrumentalness(), audioFeature.getTime_signature(),
                audioFeature.getTempo(), audioFeature.getInstrumentalness(), audioFeature.getTime_signature());
    }

    public List<Track> getTracks() {
        return null;
    }

    public List<Track> getTracks(int startTempoRange, int endTempoRange, List<String> albumsToAdd, List<String> playlistsToAdd, boolean addLikedSongs) {
        return null;
    }

    public Integer getMaxTempoOfTracks() {
        String sql = "SELECT MAX(tempo) FROM track;";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public Integer getMinTempoOfTracks() {
        String sql = "SELECT MIN(tempo)FROM track;";
        return jdbcTemplate.queryForObject(sql, int.class);

    }
}
