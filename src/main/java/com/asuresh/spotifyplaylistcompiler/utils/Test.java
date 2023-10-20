package com.asuresh.spotifyplaylistcompiler.utils;

import com.asuresh.spotifyplaylistcompiler.model.Track;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {

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
