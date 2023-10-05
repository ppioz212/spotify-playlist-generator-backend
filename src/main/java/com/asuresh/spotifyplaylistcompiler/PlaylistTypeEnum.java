package com.asuresh.spotifyplaylistcompiler;

public enum PlaylistTypeEnum {
    ALL_USER_CREATED("ALL_USER_CREATED"),
    ALL_USER_OWNED("ALL_USED_OWNED");

    private final String value;

    PlaylistTypeEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
