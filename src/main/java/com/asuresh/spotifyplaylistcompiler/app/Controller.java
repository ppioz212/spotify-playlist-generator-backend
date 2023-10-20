package com.asuresh.spotifyplaylistcompiler.app;

import com.asuresh.spotifyplaylistcompiler.utils.Network;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcTrackDao;
import com.asuresh.spotifyplaylistcompiler.model.*;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcAlbumDao;
import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcPlaylistDao;
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
    private List<String> finalTrackIds;
    private String userId;

    Controller() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/spotifyData");
        dataSource.setPassword("postgres1");
        albumDao = new JdbcAlbumDao(dataSource);
        playlistDao = new JdbcPlaylistDao(dataSource);
        trackDao = new JdbcTrackDao(dataSource);
    }

    @PostMapping("/generateNewPlaylist")
    public String generateNewPlaylist(@org.springframework.web.bind.annotation.RequestBody PlaylistDTO playlistDTO, @RequestHeader("Authorization") String accessToken) throws IOException {
        finalTrackIds = new ArrayList<>();
        if (playlistDTO.getNameOfPlaylist().equals("test")) {
            return "38mJZ8lgs9au7jSqbv6EJZ";
        }
        if (!playlistDTO.getPlaylistsToAdd().isEmpty()) {
            compilePlaylistTrackIds(playlistDTO.getPlaylistsToAdd(), accessToken);
        }
        if (!playlistDTO.getAlbumsToAdd().isEmpty()) {
            compileAlbumTrackIds(playlistDTO.getAlbumsToAdd(), accessToken);
        }
        if (playlistDTO.isAddLikedSongs()) {
            compileUserSavedSongIds(accessToken);
        }
        getTrackFeatures(accessToken);
        String newPlaylistId = createNewPlaylist(accessToken, playlistDTO.getNameOfPlaylist(), "");
        addTrackItemsToNewPlaylist(accessToken, newPlaylistId);
        return newPlaylistId;
    }

    @GetMapping("/getPlaylists")
    public List<Playlist> getPlaylists(@RequestHeader("Authorization") String accessToken) throws IOException {
        List<Playlist> playlists = new ArrayList<>();
        String playlistUrl = "https://api.spotify.com/v1/me/playlists?limit=50";
        while (playlistUrl != null) {
            JSONObject obj = Network.JsonGetRequest(accessToken, playlistUrl);
            JSONArray playlistItems = obj.getJSONArray("items");
            for (int i = 0; i < playlistItems.length(); i++) {
                Playlist currPlaylist = getPlaylistFromJson(accessToken, playlistItems.getJSONObject(i));
                playlists.add(currPlaylist);
                playlistDao.createPlaylist(currPlaylist);
            }
            playlistUrl = checkIfNextURLAvailable(obj);
        }
        return playlists;
    }

    @GetMapping("/getAlbums")
    public List<Album> getAlbums(@RequestHeader("Authorization") String accessToken) throws IOException {
        List<Album> albums = new ArrayList<>();
        String albumUrl = "https://api.spotify.com/v1/me/albums?limit=50";
        while (albumUrl != null) {
            JSONObject obj = Network.JsonGetRequest(accessToken, albumUrl);
            JSONArray albumItems = obj.getJSONArray("items");
            for (int i = 0; i < albumItems.length(); i++) {
                Album currAlbum = getAlbumFromJson(albumItems.getJSONObject(i).getJSONObject("album"));
                albums.add(currAlbum);
                albumDao.createAlbum(currAlbum);
            }
            albumUrl = checkIfNextURLAvailable(obj);
        }
        return albums;
    }

    @PostMapping("/getAccessToken")
    public Token getAccessToken(@org.springframework.web.bind.annotation.RequestBody String generatedCode) throws IOException {
        Token token = Network.getAccessTokenAPICall(generatedCode);
        JSONObject UserObj = Network.JsonGetRequest(token.getAccess_token(), "https://api.spotify.com/v1/me");
        userId = UserObj.getString("id");
        return token;
    }

    public void compileAlbumTrackIds(List<String> albumIds, String accessToken) throws IOException {
        for (String albumID : albumIds) {
            String albumTracksUrl = "https://api.spotify.com/v1/albums/" + albumID + "/tracks?limit=50";
            while (albumTracksUrl != null) {
                JSONObject obj = Network.JsonGetRequest(accessToken, albumTracksUrl);
                JSONArray albumItems = obj.getJSONArray("items");
                for (int i = 0; i < albumItems.length(); i++) {
                    Track currTrack = getTrackFromAlbumJson(albumItems.getJSONObject(i),false);
                    if (currTrack == null) {
                        continue;
                    }
                    finalTrackIds = addUniqueStringToList(finalTrackIds, currTrack.getId());
                    trackDao.createTrack(currTrack);
                    albumDao.addAlbumToTrack(albumID, currTrack.getId());
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
                Track currTrack = getTrackFromPlaylistJson(savedSongsItems.getJSONObject(i),false);
                if (currTrack == null) {
                    continue;
                }
                finalTrackIds = addUniqueStringToList(finalTrackIds, currTrack.getId());
                trackDao.createTrack(currTrack);
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
                    Track currTrack = getTrackFromPlaylistJson(playlistItems.getJSONObject(i),false);
                    if (currTrack == null) {
                        continue;
                    }
                    finalTrackIds = addUniqueStringToList(finalTrackIds, currTrack.getId());
                    trackDao.createTrack(currTrack);
                    playlistDao.addPlaylistToTrack(playlistID, currTrack.getId());
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
            JSONArray trackFeatureItems = obj.getJSONArray("audio_features");
            for (int k = 0; k < trackFeatureItems.length(); k++) {
                if (trackFeatureItems.isNull(k)) {
                    System.out.println("Null found at " + k);
                    continue;
                }
                JSONObject features = trackFeatureItems.getJSONObject(k);
                Track track = new Track(features.getString("id"), false,
                        features.getDouble("tempo"), features.getDouble("instrumentalness"),
                        features.getInt("time_signature"));
                trackDao.updateTrackFeatures(track);
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
        int numberOfTimesToAddTracks = finalTrackIds.size() / 100 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToAddTracks; i++) {
            JSONArray trackListURIsArray = new JSONArray();
            for (; j < finalTrackIds.size(); j++) {
                trackListURIsArray.put("spotify:track:" + finalTrackIds.get(j));
                if (j % 100 == 99 && j != 0) {
                    j++;
                    break;
                }
            }
            JSONObject trackListUrisObj = new JSONObject(Map.of("uris", trackListURIsArray));
            String addTracksToPlaylistUrl = "https://api.spotify.com/v1/playlists/" + newPlaylistId + "/tracks";
            Network.JsonPostRequest(accessToken, addTracksToPlaylistUrl, trackListUrisObj);
        }
    }

    public Playlist getPlaylistFromJson(String accessToken, JSONObject playlistItemData) throws IOException {
        JSONObject playlistOwner = playlistItemData.getJSONObject("owner");
        Playlist playlist = new Playlist();
        playlist.setId(playlistItemData.getString("id"));
        playlist.setName(playlistItemData.getString("name"));
        playlist.setOwner(playlistOwner.getString("id"));
        playlist.setUserId(userId);
        if (playlist.getOwner().equals(userId)) {
            playlist.setType(PlaylistTypeEnum.ALL_USER_CREATED);
        } else {
            playlist.setType(PlaylistTypeEnum.ALL_FOLLOWED_PLAYLISTS);
        }
        return playlist;
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
        album.setUserId(userId);
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
        track.setUserId(userId);
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
        track.setUserId(userId);
        track.setLikedSong(isLikedSong);
        return track;
    }

}