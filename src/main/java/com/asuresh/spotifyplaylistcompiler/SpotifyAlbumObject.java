package com.asuresh.spotifyplaylistcompiler;

public class SpotifyAlbumObject {

    private String name;
    private String id;
    private String artists;
    private String imageLink;

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
