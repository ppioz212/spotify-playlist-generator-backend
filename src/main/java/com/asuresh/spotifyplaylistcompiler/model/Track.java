package com.asuresh.spotifyplaylistcompiler.model;

public class Track {
    private String id;
    private String name;
    private boolean likedSong;
    private double tempo;
    private double instrumentalness;
    private int time_signature;

    public Track(String id, boolean likedSong, double BPM, double instrumentalness, int time_signature) {
        this.id = id;
        this.likedSong = likedSong;
        this.tempo = BPM;
        this.instrumentalness = instrumentalness;
        this.time_signature = time_signature;
    }

    public Track(String id, String name, boolean likedSong) {
        this.id = id;
        this.name = name;
        this.likedSong = likedSong;
    }

    public Track(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public int getTime_signature() {
        return time_signature;
    }

    public void setTime_signature(int time_signature) {
        this.time_signature = time_signature;
    }

    public String getName() {
        return name;
    }

    public boolean isLikedSong() {
        return likedSong;
    }

    public void setLikedSong(boolean likedSong) {
        this.likedSong = likedSong;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
