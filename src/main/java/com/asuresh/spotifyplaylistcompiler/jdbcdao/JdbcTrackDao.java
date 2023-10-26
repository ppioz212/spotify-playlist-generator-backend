package com.asuresh.spotifyplaylistcompiler.jdbcdao;


import com.asuresh.spotifyplaylistcompiler.model.AudioFeature;
import com.asuresh.spotifyplaylistcompiler.model.Track;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTrackDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public JdbcTrackDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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

    public List<String> getTrackIds(int startTempoRange, int endTempoRange, List<String> albumsToAdd,
                                    List<String> playlistsToAdd, boolean addLikedSongs) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("playlistIds", playlistsToAdd.isEmpty() ? List.of("") : playlistsToAdd);
        parameters.addValue("albumIds", albumsToAdd.isEmpty() ? List.of("") : albumsToAdd);
//        parameters.addValue("minTempo", startTempoRange);
//        parameters.addValue("maxTempo", endTempoRange);
        String sql = getTrackIdsSql(addLikedSongs);
        return namedJdbcTemplate.queryForList(sql, parameters, String.class);
    }

    @NotNull
    private static String getTrackIdsSql(boolean addLikedSongs) {
        String likedSongSqlAddon = "";
        if (addLikedSongs) {
            likedSongSqlAddon = "UNION SELECT id FROM track WHERE liked_song";
        }
        return "SELECT DISTINCT track_id FROM (SELECT DISTINCT track_id FROM playlist_track " +
                "WHERE playlist_id IN (:playlistIds) " +
                "UNION " +
                "SELECT track_id FROM album_track " +
                "WHERE album_id IN (:albumIds) " +
                likedSongSqlAddon +
                ") JOIN track ON track.id = track_id;";
    }

    public Integer getMaxTempoOfTracks() {
        String sql = "SELECT MAX(tempo) FROM track;";
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public Integer getMinTempoOfTracks() {
        String sql = "SELECT MIN(tempo) FROM track;";
        return jdbcTemplate.queryForObject(sql, int.class);

    }
}
