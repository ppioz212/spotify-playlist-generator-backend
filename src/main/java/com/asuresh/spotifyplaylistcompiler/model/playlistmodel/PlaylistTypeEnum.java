package com.asuresh.spotifyplaylistcompiler.model.playlistmodel;

public enum PlaylistTypeEnum {
    ALL_USER_CREATED("ALL_USER_CREATED"),
    ALL_FOLLOWED_ALBUMS("ALL_FOLLOWED_ALBUMS"),
    ALL_FOLLOWED_PLAYLISTS("ALL_FOLLOWED_PLAYLISTS");

    private final String value;

    PlaylistTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
