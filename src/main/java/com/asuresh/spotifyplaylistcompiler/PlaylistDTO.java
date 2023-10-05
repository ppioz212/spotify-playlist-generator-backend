package com.asuresh.spotifyplaylistcompiler;

public class PlaylistDTO {
    private PlaylistTypeEnum typeOfPlaylist;
    private String nameOfPlaylist;

    public PlaylistTypeEnum getTypeOfPlaylist() {
        return typeOfPlaylist;
    }

    public void setTypeOfPlaylist(PlaylistTypeEnum typeOfPlaylist) {
        this.typeOfPlaylist = typeOfPlaylist;
    }

    public String getNameOfPlaylist() {
        return nameOfPlaylist;
    }

    public void setNameOfPlaylist(String nameOfPlaylist) {
        this.nameOfPlaylist = nameOfPlaylist;
    }
}
