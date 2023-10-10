package com.asuresh.spotifyplaylistcompiler;

import java.util.List;

public class SpotifyAlbumObject {

    private String name;
    private String id;
    private List<String> artist;
    private String imageLink;

    public void setArtist(List<String> artist) {
        this.artist = artist;
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
