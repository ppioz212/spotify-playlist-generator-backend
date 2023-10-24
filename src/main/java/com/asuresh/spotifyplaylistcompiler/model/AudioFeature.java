package com.asuresh.spotifyplaylistcompiler.model;

public class AudioFeature{
    private double acousticness;
    private String analysis_url;
    private double danceability;
    private int duration_ms;
    private double energy;
    private String id;
    private double instrumentalness;
    private int key;
    private double liveness;
    private double loudness;
    private int mode;
    private double speechiness;
    private double tempo;
    private int time_signature;
    private String track_href;
    private String type;
    private String uri;
    private double valence;

    public double getAcousticness() {
        return acousticness;
    }

    public String getAnalysis_url() {
        return analysis_url;
    }

    public double getDanceability() {
        return danceability;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public double getEnergy() {
        return energy;
    }

    public String getId() {
        return id;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public int getKey() {
        return key;
    }

    public double getLiveness() {
        return liveness;
    }

    public double getLoudness() {
        return loudness;
    }

    public int getMode() {
        return mode;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public double getTempo() {
        return tempo;
    }

    public int getTime_signature() {
        return time_signature;
    }

    public String getTrack_href() {
        return track_href;
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public double getValence() {
        return valence;
    }
}
