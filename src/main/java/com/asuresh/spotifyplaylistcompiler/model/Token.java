package com.asuresh.spotifyplaylistcompiler.model;

public class Token {
    private String access_token;
    private String token_type;
    private float expires_in;
    private String refresh_token;
    private long timeGenerated;
    private String scope;

// Getter Methods

    public String getScope() {
        return scope;
    }

    public long getTimeGenerated() {
        return timeGenerated;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public float getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    // Setter Methods

    public void setTimeGenerated(long timeGenerated) {
        this.timeGenerated = timeGenerated;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public void setExpires_in(float expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}