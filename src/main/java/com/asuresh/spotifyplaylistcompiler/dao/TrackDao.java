package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.AudioFeature;
import com.asuresh.spotifyplaylistcompiler.model.Track;

import java.util.List;

public interface TrackDao {
    void createTrack(Track track);
    void updateTrackFeatures(AudioFeature audioFeature);

    List<Track> getTracks();
    List<Track> getTracks(int startTempoRange, int endTempoRange,
                          List<String> albumsToAdd, List<String> playlistsToAdd,
                          boolean addLikedSongs);
    Integer getMaxTempoOfTracks();
    Integer getMinTempoOfTracks();
}
