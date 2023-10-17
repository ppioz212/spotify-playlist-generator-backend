package com.asuresh.spotifyplaylistcompiler.app;

import com.asuresh.spotifyplaylistcompiler.Utils.Network;
import com.asuresh.spotifyplaylistcompiler.dao.TrackDao;
import com.asuresh.spotifyplaylistcompiler.model.*;
import com.asuresh.spotifyplaylistcompiler.dao.AlbumDao;
import com.asuresh.spotifyplaylistcompiler.dao.PlaylistDao;
import okhttp3.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.asuresh.spotifyplaylistcompiler.Utils.MiscFunctions.addUniqueStringToList;
import static com.asuresh.spotifyplaylistcompiler.Utils.MiscFunctions.mergeToUniqueList;

@RestController
public class Controller {
    public static final MediaType JSON = MediaType.get("application/json");
    static String clientID = "1dbfd19797084691bbd011cab62cb6a6";
    static String secretClientID = "56b83ad8f2e8441288feb994cec8d231";
    static String redirectUri = "http://localhost:3000";
    private final AlbumDao albumDao;
    private final PlaylistDao playlistDao;
    private final TrackDao trackDao;
    private final Network network;

    Controller() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/spotifyData");
        dataSource.setPassword("postgres1");
        albumDao = new AlbumDao(dataSource);
        playlistDao = new PlaylistDao(dataSource);
        trackDao = new TrackDao(dataSource);
        network = new Network();
    }

    @PostMapping("/generateNewPlaylist")
    public String generateNewPlaylist(@org.springframework.web.bind.annotation.RequestBody PlaylistDTO playlistObject, @RequestHeader("Authorization") String accessToken) throws IOException {

        if (playlistObject.getNameOfPlaylist().equals("test")) {
            return "38mJZ8lgs9au7jSqbv6EJZ";
        }
        List<String> finalTrackIdsToAdd = new ArrayList<>();

        if (!playlistObject.getPlaylistsToAdd().isEmpty()) {
            List<String> playlistTrackIds = compilePlaylistTrackIds(playlistObject.getPlaylistsToAdd(), accessToken);
            finalTrackIdsToAdd = new ArrayList<>(mergeToUniqueList(finalTrackIdsToAdd, playlistTrackIds));
        }
        if (!playlistObject.getAlbumsToAdd().isEmpty()) {
            List<String> albumTrackIds = compileAlbumTrackIds(playlistObject.getAlbumsToAdd(), accessToken);
            finalTrackIdsToAdd = new ArrayList<>(mergeToUniqueList(finalTrackIdsToAdd, albumTrackIds));
        }
        if (playlistObject.isAddLikedSongs()) {
            List<String> savedSongsTrackIds = compileUserSavedSongIds(accessToken);
            finalTrackIdsToAdd = new ArrayList<>(mergeToUniqueList(finalTrackIdsToAdd, savedSongsTrackIds));
        }
        getTrackFeatures(accessToken, finalTrackIdsToAdd);
        String newPlaylistId = createNewPlaylist(accessToken, playlistObject.getNameOfPlaylist(), "");
        addTrackItemsToNewPlaylist(accessToken, newPlaylistId, finalTrackIdsToAdd);
        return newPlaylistId;
    }

    @GetMapping("/getPlaylists")
    public List<Playlist> getPlaylists(@RequestHeader("Authorization") String accessToken) throws IOException {
        String userId = network.getUserId(accessToken);
        List<Playlist> playlists = new ArrayList<>();

        boolean shouldRunRequestAgain = true;
        String playlistUrl = "https://api.spotify.com/v1/me/playlists?limit=50";
        while (shouldRunRequestAgain) {
            JSONObject obj = network.JsonGetRequest(accessToken, playlistUrl);

            if (obj.isNull("next")) {
                shouldRunRequestAgain = false;
            } else {
                playlistUrl = obj.getString("next");
            }
            JSONArray playlistItems = obj.getJSONArray("items");
            for (int i = 0; i < playlistItems.length(); i++) {

                JSONObject playlistItemData = playlistItems.getJSONObject(i);
                JSONObject playlistOwner = playlistItemData.getJSONObject("owner");
                Playlist currPlaylist = new Playlist(playlistItemData.getString("id"),
                        playlistItemData.getString("name"), playlistOwner.getString("id"));
                if (currPlaylist.getOwner().equals(userId)) {
                    currPlaylist.setType(PlaylistTypeEnum.ALL_USER_CREATED);
                } else {
                    currPlaylist.setType(PlaylistTypeEnum.ALL_FOLLOWED_PLAYLISTS);
                }
                playlists.add(currPlaylist);
                playlistDao.createPlaylist(currPlaylist);
            }

        }
        return playlists;
    }

    @GetMapping("/getAlbums")
    public List<Album> getAlbums(@RequestHeader("Authorization") String accessToken) throws IOException {
        List<Album> albums = new ArrayList<>();
        boolean shouldRunRequestAgain = true;
        String albumUrl = "https://api.spotify.com/v1/me/albums?limit=50";
        while (shouldRunRequestAgain) {
            JSONObject obj = network.JsonGetRequest(accessToken, albumUrl);

            if (obj.isNull("next")) {
                shouldRunRequestAgain = false;
            } else {
                albumUrl = obj.getString("next");
            }
            JSONArray albumItems = obj.getJSONArray("items");
            for (int i = 0; i < albumItems.length(); i++) {

                JSONObject albumData = albumItems.getJSONObject(i).getJSONObject("album");
                StringBuilder sb = new StringBuilder();
                JSONArray artistsJSONArray = (albumData.getJSONArray("artists"));
                for (int j = 0; j < artistsJSONArray.length(); j++) {
                    if (j == artistsJSONArray.length() - 1) {
                        sb.append(artistsJSONArray.getJSONObject(j).getString("name"));
                        continue;
                    }
                    sb.append(artistsJSONArray.getJSONObject(j).getString("name")).append(", ");
                }
                Album currAlbum = new Album(albumData.getString("id"),
                        albumData.getString("name"), sb.toString());
                albums.add(currAlbum);
                albumDao.createAlbum(currAlbum);
            }
        }
        return albums;
    }

    @PostMapping("/getAccessToken")
    public Token getAccessToken(@org.springframework.web.bind.annotation.RequestBody String generatedCode) {
        return network.getAccessTokenAPICall(generatedCode);
    }

    public List<String> compileAlbumTrackIds(List<String> playlistIds, String accessToken) throws IOException {
        List<String> albumTracks = new ArrayList<>();
        for (String albumID : playlistIds) {
            String albumTracksUrl = "https://api.spotify.com/v1/albums/" + albumID + "/tracks?limit=50";
            boolean shouldRunRequestAgain = true;
            while (shouldRunRequestAgain) {
                JSONObject obj = network.JsonGetRequest(accessToken, albumTracksUrl);

                if (obj.isNull("next")) {
                    shouldRunRequestAgain = false;
                } else {
                    albumTracksUrl = obj.getString("next");
                }
                JSONArray albumItems = obj.getJSONArray("items");
                for (int i = 0; i < albumItems.length(); i++) {
                    if (albumItems.getJSONObject(i).getString("id") == null) {
                        continue;
                    }
                    Track track = new Track(albumItems.getJSONObject(i).getString("id"),
                            albumItems.getJSONObject(i).getString("name"), false);
                    albumTracks = addUniqueStringToList(albumTracks, track.getId());
                    trackDao.createTrack(track);
                    albumDao.addAlbumToTrack(albumID, track.getId());
                }
            }
        }
        return albumTracks;
    }


    public List<String> compileUserSavedSongIds(String accessToken) throws IOException {
        List<String> savedTrackIds = new ArrayList<>();
        String savedSongsUrl = "https://api.spotify.com/v1/me/tracks?limit=50";
        boolean shouldRunRequestAgain = true;
        while (shouldRunRequestAgain) {
            JSONObject obj = network.JsonGetRequest(accessToken, savedSongsUrl);
            if (obj.isNull("next")) {
                shouldRunRequestAgain = false;
            } else {
                savedSongsUrl = obj.getString("next");
            }
            JSONArray savedSongsItems = obj.getJSONArray("items");
            for (int i = 0; i < savedSongsItems.length(); i++) {
                JSONObject playlistItemsData = savedSongsItems.getJSONObject(i);
                Track track = new Track(playlistItemsData.getJSONObject("track").getString("id"),
                        playlistItemsData.getJSONObject("track").getString("name"), true);
                savedTrackIds = addUniqueStringToList(savedTrackIds, track.getId());
                trackDao.createTrack(track);
            }
        }
        return savedTrackIds;
    }

    public List<String> compilePlaylistTrackIds(List<String> playlistIds, String accessToken) throws IOException {
        List<String> playlistTracks = new ArrayList<>();
        for (String playlistID : playlistIds) {
            String playlistTracksUrl = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks?limit=50";
            boolean shouldRunRequestAgain = true;
            while (shouldRunRequestAgain) {
                JSONObject obj = network.JsonGetRequest(accessToken, playlistTracksUrl);
                if (obj.isNull("next")) {
                    shouldRunRequestAgain = false;
                } else {
                    playlistTracksUrl = obj.getString("next");
                }
                JSONArray playlistItems = obj.getJSONArray("items");
                for (int i = 0; i < playlistItems.length(); i++) {
                    JSONObject playlistItemsData = playlistItems.getJSONObject(i);
                    if (playlistItemsData.getJSONObject("track").isNull("id") ||
                            (playlistItemsData.keySet().contains("episode"))) {
                        continue;
                    }
                    Track track = new Track(playlistItemsData.getJSONObject("track").getString("id"),
                            playlistItemsData.getJSONObject("track").getString("name"), false);
                    playlistTracks = addUniqueStringToList(playlistTracks, track.getId());
                    trackDao.createTrack(track);
                    playlistDao.addPlaylistToTrack(playlistID, track.getId());
                }
            }
        }
        return playlistTracks;
    }

    public void getTrackFeatures(String accessToken, List<String> trackIds) throws IOException {
        int numberOfTimesToGetFeatures = trackIds.size() / 100 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToGetFeatures; i++) {
            StringBuilder sb_ids = new StringBuilder();
            for (; j < trackIds.size(); j++) {
                if (j % 100 == 0) {
                    sb_ids.append(trackIds.get(j));
                } else {
                    sb_ids.append("%2C").append(trackIds.get(j));
                }
                if (j % 100 == 99 && j != 0) {
                    j++;
                    break;
                }
            }
            String trackFeaturesUrl = "https://api.spotify.com/v1/audio-features?ids=" + sb_ids;
            JSONObject obj = network.JsonGetRequest(accessToken, trackFeaturesUrl);
            JSONArray trackFeatureItems = obj.getJSONArray("audio_features");
            for (int k = 0; k < trackFeatureItems.length(); k++) {
                JSONObject features = trackFeatureItems.getJSONObject(k);
                Track track = new Track(features.getString("id"), false,
                        features.getDouble("tempo"), features.getDouble("instrumentalness"),
                        features.getInt("time_signature"));
                trackDao.createTrackExtra(track);
            }
        }
    }

    public String createNewPlaylist(String accessToken, String newPlaylistName, String newPlaylistDescription) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create("{\n    \"name\": \"" + newPlaylistName + "\",\n    \"description\": \"" + newPlaylistDescription + "\",\n    \"public\": false\n}", JSON);
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .post(body)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            String jsonOutput = response.body().string();
            JSONObject obj = new JSONObject(jsonOutput);
            return obj.getString("id");
        }
    }

    public void addTrackItemsToNewPlaylist(String accessToken, String newPlaylistId, List<String> trackIdsToAdd) throws IOException {
        OkHttpClient client = new OkHttpClient();

        int numberOfTimesToAddTracks = trackIdsToAdd.size() / 100 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToAddTracks; i++) {
            JSONArray trackListURIs = new JSONArray();
            for (; j < trackIdsToAdd.size(); j++) {
                trackListURIs.put("spotify:track:" + trackIdsToAdd.get(j));
                if (j % 99 == 0 && j != 0) {
                    j++;
                    break;
                }
            }
            JSONObject finalTrackListURis = new JSONObject();
            finalTrackListURis.put("uris", trackListURIs);
            RequestBody body = RequestBody.create(String.valueOf(finalTrackListURis), JSON);
            Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/playlists/" + newPlaylistId + "/tracks")
                    .post(body)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                assert response.body() != null;
            }
        }
    }
}