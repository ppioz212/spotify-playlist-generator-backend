package com.asuresh.spotifyplaylistcompiler.Utils;

import com.asuresh.spotifyplaylistcompiler.model.Token;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

public class Network {
    public final MediaType JSON = MediaType.get("application/json");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public JSONObject JsonGetRequest(String accessToken, String URL) throws IOException {
        Request request = new Request.Builder()
                .url(URL)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return new JSONObject(response.body().string());
        }
    }

    public JSONObject JsonPostRequest(String accessToken, String URL, JSONObject data) throws IOException {
        RequestBody body = RequestBody.create(String.valueOf(data), JSON);
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return new JSONObject(response.body().string());
        }
    }

    public Token getAccessTokenAPICall(String generatedCode) {
        final String clientID = "1dbfd19797084691bbd011cab62cb6a6";
        final String secretClientID = "56b83ad8f2e8441288feb994cec8d231";
        final String redirectUri = "http://localhost:3000";
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