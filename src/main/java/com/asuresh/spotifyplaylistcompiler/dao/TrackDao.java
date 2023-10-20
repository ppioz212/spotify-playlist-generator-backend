package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.model.Track;

public interface TrackDao {
    void createTrack(Track track);
    void updateTrackFeatures(Track track);
}
