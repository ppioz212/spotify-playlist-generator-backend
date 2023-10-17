package com.asuresh.spotifyplaylistcompiler.Utils;

import com.asuresh.spotifyplaylistcompiler.model.Token;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

public class Network {
    public static final MediaType JSON = MediaType.get("application/json");
    private final String clientID = "1dbfd19797084691bbd011cab62cb6a6";
    private final String secretClientID = "56b83ad8f2e8441288feb994cec8d231";
    static String redirectUri = "http://localhost:3000";

    public Token getAccessToken(String generatedCode) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        String authHeader = clientID + ":" + secretClientID;
        String encodedString = Base64.getEncoder().encodeToString(authHeader.getBytes());

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", generatedCode)
                .add("redirect_uri", redirectUri)
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(formBody)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedString)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return gson.fromJson(response.body().string(), Token.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
