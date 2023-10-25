package com.asuresh.spotifyplaylistcompiler.app;

import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcUserDao;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import com.asuresh.spotifyplaylistcompiler.utils.Network;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcTrackDao;
import com.asuresh.spotifyplaylistcompiler.model.*;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcAlbumDao;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcPlaylistDao;
import com.google.gson.Gson;
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
import java.util.Map;

import static com.asuresh.spotifyplaylistcompiler.utils.MiscFunctions.*;

@RestController
public class Controller {
    private final JdbcAlbumDao albumDao;
    private final JdbcPlaylistDao playlistDao;
    private final JdbcTrackDao trackDao;
    private final JdbcUserDao userDao;
    private List<String> finalTrackIds;
    private User user;
    private final Gson gson;

    Controller() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/spotifyData");
        dataSource.setPassword("postgres1");
        albumDao = new JdbcAlbumDao(dataSource);
        playlistDao = new JdbcPlaylistDao(dataSource);
        trackDao = new JdbcTrackDao(dataSource);
        userDao = new JdbcUserDao(dataSource);
        gson = new Gson();
    }

    @PostMapping("/generateNewPlaylist")
    public String generateNewPlaylist(@org.springframework.web.bind.annotation.RequestBody NewPlaylistDTO newPlaylistDTO, @RequestHeader("Authorization") String accessToken) throws IOException {
        user = Network.getUser(accessToken);
        finalTrackIds = new ArrayList<>();
        if (newPlaylistDTO.getNameOfPlaylist().equals("test")) {
            return "38mJZ8lgs9au7jSqbv6EJZ";
        }
        if (!newPlaylistDTO.getPlaylistsToAdd().isEmpty()) {
            compilePlaylistTrackIds(newPlaylistDTO.getPlaylistsToAdd(), accessToken);
        }
        if (!newPlaylistDTO.getAlbumsToAdd().isEmpty()) {
            compileAlbumTrackIds(newPlaylistDTO.getAlbumsToAdd(), accessToken);
        }
        if (newPlaylistDTO.isAddLikedSongs()) {
            compileUserSavedSongIds(accessToken);
        }
        getTrackFeatures(accessToken);
        String newPlaylistId = createNewPlaylist(accessToken, newPlaylistDTO.getNameOfPlaylist(), "");
        addTrackItemsToNewPlaylist(accessToken, newPlaylistId);
        return newPlaylistId;
    }

    @GetMapping("/getPlaylists")
    public List<Playlist> getPlaylists(@RequestHeader("Authorization") String accessToken) throws IOException {
        user = Network.getUser(accessToken);
        userDao.createUser(user);
        List<Playlist> allPlaylists = new ArrayList<>();
        String playlistUrl = "https://api.spotify.com/v1/me/playlists?limit=50";
        while (playlistUrl != null) {
            JSONObject obj = Network.JsonGetRequest(accessToken, playlistUrl);
            Playlist[] playlists = gson.fromJson(
                    String.valueOf(obj.getJSONArray("items")), Playlist[].class);
            for (Playlist playlist : playlists) {
                allPlaylists.add(playlist);
                playlistDao.createPlaylist(playlist, user.getId());
            }
            playlistUrl = checkIfNextURLAvailable(obj);
        }
        return allPlaylists;
    }

    @GetMapping("/getAlbums")
    public List<Album> getAlbums(@RequestHeader("Authorization") String accessToken) throws IOException {
        user = Network.getUser(accessToken);
        List<Album> allAlbums = new ArrayList<>();
        String albumUrl = "https://api.spotify.com/v1/me/albums?limit=50";
        while (albumUrl != null) {
            JSONObject obj = Network.JsonGetRequest(accessToken, albumUrl);
            JSONArray albumItems = obj.getJSONArray("items");
            for (int i = 0; i < albumItems.length(); i++) {
                Album album = getAlbumFromJson(albumItems.getJSONObject(i).getJSONObject("album"));
                allAlbums.add(album);
                albumDao.createAlbum(album, user.getId());
            }
            albumUrl = checkIfNextURLAvailable(obj);
        }
        return allAlbums;
    }

    @PostMapping("/getAccessToken")
    public Token getAccessToken(@org.springframework.web.bind.annotation.RequestBody String generatedCode) throws IOException {
        return Network.getAccessTokenAPICall(generatedCode);
    }

    @GetMapping("/getUser")
    public User GetUser(@RequestHeader("Authorization") String accessToken) throws IOException {
        return Network.getUser(accessToken);
    }

    public void compileAlbumTrackIds(List<String> albumIds, String accessToken) throws IOException {
        for (String albumID : albumIds) {
            String albumTracksUrl = "https://api.spotify.com/v1/albums/" + albumID + "/tracks?limit=50";
            while (albumTracksUrl != null) {
                JSONObject obj = Network.JsonGetRequest(accessToken, albumTracksUrl);
                JSONArray albumItems = obj.getJSONArray("items");
                for (int i = 0; i < albumItems.length(); i++) {
                    Track track = getTrackFromAlbumJson(albumItems.getJSONObject(i), false);
                    if (track == null) {
                        continue;
                    }
                    finalTrackIds = addUniqueStringToList(finalTrackIds, track.getId());
                    trackDao.createTrack(track);
                    albumDao.linkTrackToAlbum(albumID, track.getId());
                }
                albumTracksUrl = checkIfNextURLAvailable(obj);
            }
        }
    }

    public void compileUserSavedSongIds(String accessToken) throws IOException {
        String savedSongsUrl = "https://api.spotify.com/v1/me/tracks?limit=50";
        while (savedSongsUrl != null) {
            JSONObject obj = Network.JsonGetRequest(accessToken, savedSongsUrl);
            JSONArray savedSongsItems = obj.getJSONArray("items");
            for (int i = 0; i < savedSongsItems.length(); i++) {
                Track track = getTrackFromPlaylistJson(savedSongsItems.getJSONObject(i), true);
                if (track == null) {
                    continue;
                }
                finalTrackIds = addUniqueStringToList(finalTrackIds, track.getId());
                trackDao.createTrack(track);
            }
            savedSongsUrl = checkIfNextURLAvailable(obj);
        }
    }

    public void compilePlaylistTrackIds(List<String> playlistIds, String accessToken) throws IOException {
        for (String playlistID : playlistIds) {
            String playlistTracksUrl = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks?limit=50";
            while (playlistTracksUrl != null) {
                JSONObject obj = Network.JsonGetRequest(accessToken, playlistTracksUrl);
                JSONArray playlistItems = obj.getJSONArray("items");
                for (int i = 0; i < playlistItems.length(); i++) {
                    Track track = getTrackFromPlaylistJson(playlistItems.getJSONObject(i), false);
                    if (track == null) {
                        continue;
                    }
                    finalTrackIds = addUniqueStringToList(finalTrackIds, track.getId());
                    trackDao.createTrack(track);
                    playlistDao.linkTrackToPlaylist(playlistID, track.getId());
                }
                playlistTracksUrl = checkIfNextURLAvailable(obj);
            }
        }
    }

    public void getTrackFeatures(String accessToken) throws IOException {
        int numberOfTimesToGetFeatures = finalTrackIds.size() / 100 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToGetFeatures; i++) {
            StringBuilder sb_ids = new StringBuilder();
            for (; j < finalTrackIds.size(); j++) {
                if (j % 100 == 0) {
                    sb_ids.append(finalTrackIds.get(j));
                } else {
                    sb_ids.append("%2C").append(finalTrackIds.get(j));
                }
                if (j % 100 == 99 && j != 0) {
                    j++;
                    break;
                }
            }
            String trackFeaturesUrl = "https://api.spotify.com/v1/audio-features?ids=" + sb_ids;
            JSONObject obj = Network.JsonGetRequest(accessToken, trackFeaturesUrl);
            AudioFeature[] audioFeatures = gson.fromJson(
                    String.valueOf(obj.getJSONArray("audio_features")), AudioFeature[].class);
            for (AudioFeature audioFeature : audioFeatures) {
                trackDao.createAudioFeatures(audioFeature);
            }
        }
    }

    public String createNewPlaylist(String accessToken, String newPlaylistName, String newPlaylistDescription) throws IOException {
        JSONObject newPlaylistInfo = new JSONObject(Map.of("name", newPlaylistName,
                "description", newPlaylistDescription, "public", false));
        String newPlaylistUrl = "https://api.spotify.com/v1/me/playlists";
        JSONObject obj = Network.JsonPostRequest(accessToken, newPlaylistUrl, newPlaylistInfo);
        return obj.getString("id");
    }

    public void addTrackItemsToNewPlaylist(String accessToken, String newPlaylistId) throws IOException {
        System.out.println(finalTrackIds.size());
        int numberOfTimesToAddTracks = finalTrackIds.size() / 99 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToAddTracks; i++) {
            JSONArray trackListURIsArray = new JSONArray();
            for (; j < finalTrackIds.size(); j++) {
                trackListURIsArray.put("spotify:track:" + finalTrackIds.get(j));
                if (j % 99 == 98 && j != 0) {
                    j++;
                    break;
                }
            }
            JSONObject trackListUrisObj = new JSONObject(Map.of("uris", trackListURIsArray));
            String addTracksToPlaylistUrl = "https://api.spotify.com/v1/playlists/" + newPlaylistId + "/tracks";
            Network.JsonPostRequest(accessToken, addTracksToPlaylistUrl, trackListUrisObj);
        }
    }

    public Album getAlbumFromJson(JSONObject albumData) {
        StringBuilder sb = new StringBuilder();
        JSONArray artistsJSONArray = (albumData.getJSONArray("artists"));
        for (int j = 0; j < artistsJSONArray.length(); j++) {
            if (j == artistsJSONArray.length() - 1) {
                sb.append(artistsJSONArray.getJSONObject(j).getString("name"));
                continue;
            }
            sb.append(artistsJSONArray.getJSONObject(j).getString("name")).append(", ");
        }
        Album album = new Album();
        album.setId(albumData.getString("id"));
        album.setName(albumData.getString("name"));
        album.setUserId(user.getId());
        album.setArtists(sb.toString());
        return album;
    }

    public Track getTrackFromPlaylistJson(JSONObject playlistItem, boolean isLikedSong) {
        if (playlistItem.getJSONObject("track").isNull("id") ||
                (playlistItem.keySet().contains("episode"))) {
            return null;
        }
        Track track = new Track();
        track.setId(playlistItem.getJSONObject("track").getString("id"));
        track.setName(playlistItem.getJSONObject("track").getString("name"));
        track.setUserId(user.getId());
        track.setLikedSong(isLikedSong);
        return track;
    }

    public Track getTrackFromAlbumJson(JSONObject trackItem, boolean isLikedSong) {
        if (trackItem.isNull("id")) {
            return null;
        }
        Track track = new Track();
        track.setId(trackItem.getString("id"));
        track.setName(trackItem.getString("name"));
        track.setUserId(user.getId());
        track.setLikedSong(isLikedSong);
        return track;
    }
}