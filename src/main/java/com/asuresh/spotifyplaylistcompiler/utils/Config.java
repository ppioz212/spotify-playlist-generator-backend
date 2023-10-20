package com.asuresh.spotifyplaylistcompiler.utils;

public class Config {
    private String clientId;
    private String secretClientId;

    public Config() {}
    public Config(String clientId, String secretClientId) {
        this.clientId = clientId;
        this.secretClientId = secretClientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretClientId() {
        return secretClientId;
    }

    public void setSecretClientId(String secretClientId) {
        this.secretClientId = secretClientId;
    }
}
