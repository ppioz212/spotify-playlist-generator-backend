package com.asuresh.spotifyplaylistcompiler.model;

import com.asuresh.spotifyplaylistcompiler.dao.PlaylistDao;

public class Playlist {

    private String id;
    private String name;
    private PlaylistTypeEnum type;
    private String owner;
    private String imageLink;

    public Playlist() {}

    public Playlist(String id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

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
