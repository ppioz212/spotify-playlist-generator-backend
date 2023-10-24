package com.asuresh.spotifyplaylistcompiler.jdbcdao;


import com.asuresh.spotifyplaylistcompiler.dao.TrackDao;
import com.asuresh.spotifyplaylistcompiler.model.Track;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTrackDao implements TrackDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTrackDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
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

    @Override
    public void updateTrackFeatures(Track track) {
        String sql = "INSERT INTO track (id, liked_song, tempo, instrumentalness, time_signature) " +
                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (id) " +
                "DO UPDATE SET liked_song = ?, tempo = ?, instrumentalness = ?, time_signature = ?;";
        jdbcTemplate.update(sql, track.getId(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature(),
                track.isLikedSong(), track.getTempo(), track.getInstrumentalness(), track.getTime_signature());
    }

    @Override
    public List<Track> getTracks() {
        return null;
    }

    @Override
    public List<Track> getTracks(int startTempoRange, int endTempoRange, List<String> albumsToAdd, List<String> playlistsToAdd, boolean addLikedSongs) {
        return null;
    }

    @Override
    public int getMaxTempoOfTracks() {
        int maxTempo = 0;
        String sql = "SELECT MAX(tempo) AS max_tempo FROM track;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            maxTempo = results.getInt("max_tempo");
        }
        return maxTempo;
    }

    @Override
    public int getMinTempoOfTracks() {
        int minTempo = 0;
        String sql = "SELECT MIN(tempo) AS min_tempo FROM track;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            minTempo = results.getInt("min_tempo");
        }
        return minTempo;
    }
}
