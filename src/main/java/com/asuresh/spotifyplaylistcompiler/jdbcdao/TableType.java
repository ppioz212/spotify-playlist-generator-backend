package com.asuresh.spotifyplaylistcompiler.jdbcdao;

public enum TableType {
    ALBUM("albums"),
    PLAYLIST("playlists"),
    TRACK("tracks"),
    TOTAL("total");

    private final String value;

    TableType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
