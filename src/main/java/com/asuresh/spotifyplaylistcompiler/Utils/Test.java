package com.asuresh.spotifyplaylistcompiler.Utils;

import com.asuresh.spotifyplaylistcompiler.model.Track;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String token = "BQAYGhZhPEncQVE5x_FrAFIcrKRBM_DH7pyhOC2n5bcyTZfGGvkujs5Blw5MtvNZjzpQ_I1I1CLN7IO8krvdwzBZvwTG3tuhLEZQJBQrbS1ep1LsyzzKL_gFvyyJiw9xoH6C7rVv90uk_tAJjZ5c3onU4JkncnPGUDt45MqQ34XoTn8L8L61a1VrmXjsiWkAi42wi6JLCajKgdOUSLi390S40t6Bq181h5gyl5d_F6jx";
//        getTrackFeatures(token,);
    }
    public static void getTrackFeatures(String accessToken, List<String> trackIds) throws IOException {
        OkHttpClient client = new OkHttpClient();
        int numberOfTimesToGetFeatures = trackIds.size() / 101 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToGetFeatures; i++) {
            StringBuilder sb_ids = new StringBuilder();
            for (; j < trackIds.size(); j++) {
                if (j % 101 == 0) {
                    sb_ids.append(trackIds.get(j));
                } else {
                    sb_ids.append("%2C").append(trackIds.get(j));
                }
                if (j % 101 == 0 && j != 0) {
                    j++;
                    break;
                }
            }
            Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/audio-features?ids=" + sb_ids)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                assert response.body() != null;
                String jsonOutput = response.body().string();
                JSONObject obj = new JSONObject(jsonOutput);
                JSONArray trackFeatureItems = obj.getJSONArray("audio_features");
                for (int k = 0; k < trackFeatureItems.length(); k++) {
                    JSONObject features = trackFeatureItems.getJSONObject(k);
                    System.out.println(k + ") " + features.toString());
                    Track track = new Track(features.getString("id"), false,
                            features.getDouble("tempo"), features.getDouble("instrumentalness"),
                            features.getInt("time_signature"));
                }
            }
        }
    }
}
