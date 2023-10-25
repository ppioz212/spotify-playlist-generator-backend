package com.asuresh.spotifyplaylistcompiler.model.playlistmodel;

import com.google.gson.annotations.SerializedName;

public class Owner {
    private ExternalUrls external_urls;
    private Followers followers;
    private String href;
    private String id;
    private String type;
    private String uri;
    @SerializedName("display_name")
    private String displayName;

    public Owner() {}
    public Owner(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    public ExternalUrls getExternal_urls() {
        return external_urls;
    }

    public Followers getFollowers() {
        return followers;
    }

    public String getHref() {
        return href;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }
}
