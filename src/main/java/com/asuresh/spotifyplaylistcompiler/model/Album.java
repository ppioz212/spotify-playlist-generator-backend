package com.asuresh.spotifyplaylistcompiler.model;

public class Album {

    private String id;
    private String name;
    private String artists;
    private String imageLink;

    public Album() {}

    public Album(String id, String name, String artists) {
        this.id = id;
        this.name = name;
        this.artists = artists;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
