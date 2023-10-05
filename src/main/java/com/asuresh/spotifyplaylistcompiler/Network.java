package com.asuresh.spotifyplaylistcompiler;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class Network {
    static final private String ACCESS_TOKEN_FILENAME = "accessTokenInfo.txt";
    static OkHttpClient client;
    static Gson gson;
    static String clientID = "1dbfd19797084691bbd011cab62cb6a6";
    static String secretClientID = "56b83ad8f2e8441288feb994cec8d231";

    static String redirectUri = "http://localhost:3000";
}
