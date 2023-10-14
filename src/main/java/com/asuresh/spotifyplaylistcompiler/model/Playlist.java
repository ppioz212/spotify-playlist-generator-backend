package com.asuresh.spotifyplaylistcompiler.model;

public class Playlist {

    private String id;
    private String name;
    private PlaylistTypeEnum type;
    private String owner;
    private String imageLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaylistTypeEnum getType() {
        return type;
    }

    public void setType(PlaylistTypeEnum type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
