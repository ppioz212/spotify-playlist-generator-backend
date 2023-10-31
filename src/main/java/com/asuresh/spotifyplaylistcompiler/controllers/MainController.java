package com.asuresh.spotifyplaylistcompiler.controllers;

import com.asuresh.spotifyplaylistcompiler.jdbcdao.*;
import com.asuresh.spotifyplaylistcompiler.services.PlaylistDTO;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import com.asuresh.spotifyplaylistcompiler.services.SpotifyService;
import com.asuresh.spotifyplaylistcompiler.model.*;
import com.google.gson.Gson;
import org.apache.commons.dbcp2.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.asuresh.spotifyplaylistcompiler.utils.MiscFunctions.*;

@RestController
public class MainController {
    private final JdbcAlbumDao albumDao;
    private final JdbcPlaylistDao playlistDao;
    private final JdbcTrackDao trackDao;
    private final JdbcUserDao userDao;
    private User user;
    private final Gson gson;

    MainController() {
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
    public String generateNewPlaylist(
            @RequestBody PlaylistDTO playlistDTO,
            @RequestHeader("Authorization") String accessToken) throws IOException {

        user = SpotifyService.getUser(accessToken);
        if (playlistDTO.getNameOfPlaylist().equals("test")) {
            return "38mJZ8lgs9au7jSqbv6EJZ";
        }
        String newPlaylistId = createNewPlaylist(accessToken, playlistDTO.getNameOfPlaylist(), "");
        List<String> tracksToAdd = trackDao.getTrackIds(playlistDTO.getAlbumsToAdd(), playlistDTO.getPlaylistsToAdd(),
                playlistDTO.isAddLikedSongs(), user.getId());
        System.out.println(tracksToAdd.size());
        addTrackItemsToPlaylist(accessToken, newPlaylistId, tracksToAdd);
        return newPlaylistId;
    }

    @GetMapping("/getPlaylists")
    public List<Playlist> getPlaylists(@RequestHeader("Authorization") String accessToken) throws IOException {
        user = SpotifyService.getUser(accessToken);
        if (userDao.wasDataPreviouslyPulled(TableType.PLAYLIST, user.getId())) {
            return playlistDao.getPlaylists(user.getId());
        }
        List<Playlist> allPlaylists = new ArrayList<>();
        String playlistUrl = "https://api.spotify.com/v1/me/playlists?limit=50";
        while (playlistUrl != null) {
            JSONObject obj = SpotifyService.JsonGetRequest(accessToken, playlistUrl);
            Playlist[] playlists = gson.fromJson(
                    String.valueOf(obj.getJSONArray("items")), Playlist[].class);
            for (Playlist playlist : playlists) {
                allPlaylists.add(playlist);
                playlistDao.createPlaylist(playlist, user.getId());
            }
            playlistUrl = checkIfNextURLAvailable(obj);
        }
        userDao.updateDataPulled(TableType.PLAYLIST, true, user.getId());
        return allPlaylists;
    }

    @GetMapping("/getAlbums")
    public List<Album> getAlbums(@RequestHeader("Authorization") String accessToken) throws IOException {
        user = SpotifyService.getUser(accessToken);
        if (userDao.wasDataPreviouslyPulled(TableType.ALBUM, user.getId())) {
            return albumDao.getAlbums(user.getId());
        }
        List<Album> allAlbums = new ArrayList<>();
        String albumUrl = "https://api.spotify.com/v1/me/albums?limit=50";
        while (albumUrl != null) {
            JSONObject obj = SpotifyService.JsonGetRequest(accessToken, albumUrl);
            JSONArray albumItems = obj.getJSONArray("items");
            for (int i = 0; i < albumItems.length(); i++) {
                Album album = getAlbumFromJson(albumItems.getJSONObject(i).getJSONObject("album"));
                allAlbums.add(album);
                albumDao.createAlbum(album, user.getId());
            }
            albumUrl = checkIfNextURLAvailable(obj);
        }
        userDao.updateDataPulled(TableType.ALBUM, true, user.getId());
        return allAlbums;
    }

    @GetMapping("/compileTracks")
    public void createAllTracks(@RequestHeader("Authorization") String accessToken) throws IOException {
        user = SpotifyService.getUser(accessToken);
        if (!(userDao.wasDataPreviouslyPulled(TableType.TRACK, user.getId()))) {
            System.out.println("Tracks not found");

            List<String> albumIds = albumDao.getAlbumIds();
            List<String> playlistIds = playlistDao.getPlaylistIds();

            System.out.println("Compiling Album tracks");
            compileAlbumTrackIds(albumIds, accessToken);
            System.out.println("Album tracks compiled");

            System.out.println("Compiling Playlist tracks");
            compilePlaylistTrackIds(playlistIds, accessToken);
            System.out.println("Playlist tracks compiled");

            System.out.println("Compiling Saved tracks");
            compileUserSavedSongIds(accessToken);
            System.out.println("Saved tracks compiled");

            System.out.println("Compiling audio features");
            compileTrackFeatures(accessToken);
            System.out.println("Audio features compiled");

            userDao.updateDataPulled(TableType.TRACK, true, user.getId());
        } else {
            System.out.println("Track data found");
        }
    }

    @GetMapping("/deleteUser")
    public void deleteUserData(@RequestHeader("UserId") String userId) {
        albumDao.deleteAlbums(userId);
        playlistDao.deletePlaylists(userId);
        trackDao.deleteTracksByUserId(userId);
        userDao.deleteUser(userId);
    }

    @PostMapping("/getAccessToken")
    public Token getAccessToken(@RequestBody String generatedCode) throws IOException {
        return SpotifyService.getAccessTokenAPICall(generatedCode);
    }

    @GetMapping("/getUser")
    public User GetUser(@RequestHeader("Authorization") String accessToken) throws IOException {
        User user = SpotifyService.getUser(accessToken);
        user.setAlbumsPulled(false);
        user.setPlaylistsPulled(false);
        user.setTracksPulled(false);
        userDao.createUser(user);
        return user;
    }

    public void compilePlaylistTrackIds(List<String> playlistIds, String accessToken) throws IOException {
        for (String playlistID : playlistIds) {
            String playlistTracksUrl = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks?limit=50";
            while (playlistTracksUrl != null) {
                JSONObject obj = SpotifyService.JsonGetRequest(accessToken, playlistTracksUrl);
                JSONArray playlistItems = obj.getJSONArray("items");
                for (int i = 0; i < playlistItems.length(); i++) {
                    Track track = getTrackFromPlaylistJson(playlistItems.getJSONObject(i), false);
                    if (track == null) {
                        continue;
                    }
                    trackDao.createTrack(track);
                    playlistDao.linkTrackToPlaylist(playlistID, track.getId());
                }
                playlistTracksUrl = checkIfNextURLAvailable(obj);
            }
        }
    }

    public void compileAlbumTrackIds(List<String> albumIds, String accessToken) throws IOException {
        for (String albumID : albumIds) {
            String albumTracksUrl = "https://api.spotify.com/v1/albums/" + albumID + "/tracks?limit=50";
            while (albumTracksUrl != null) {
                JSONObject obj = SpotifyService.JsonGetRequest(accessToken, albumTracksUrl);
                JSONArray albumItems = obj.getJSONArray("items");
                for (int i = 0; i < albumItems.length(); i++) {
                    Track track = getTrackFromAlbumJson(albumItems.getJSONObject(i), false);
                    if (track == null) {
                        continue;
                    }
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
            JSONObject obj = SpotifyService.JsonGetRequest(accessToken, savedSongsUrl);
            JSONArray savedSongsItems = obj.getJSONArray("items");
            for (int i = 0; i < savedSongsItems.length(); i++) {
                Track track = getTrackFromPlaylistJson(savedSongsItems.getJSONObject(i), true);
                if (track == null) {
                    continue;
                }
                trackDao.createTrack(track);
            }
            savedSongsUrl = checkIfNextURLAvailable(obj);
        }
    }


    public void compileTrackFeatures(String accessToken) throws IOException {
        List<String> allTracks = trackDao.getTrackIds(user.getId());
        int numberOfTimesToGetFeatures = allTracks.size() / 100 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToGetFeatures; i++) {
            StringBuilder sb_ids = new StringBuilder();
            for (; j < allTracks.size(); j++) {
                if (j % 100 == 0) {
                    sb_ids.append(allTracks.get(j));
                } else {
                    sb_ids.append("%2C").append(allTracks.get(j));
                }
                if (j % 100 == 99 && j != 0) {
                    j++;
                    break;
                }
            }
            String trackFeaturesUrl = "https://api.spotify.com/v1/audio-features?ids=" + sb_ids;
            JSONObject obj = SpotifyService.JsonGetRequest(accessToken, trackFeaturesUrl);
            AudioFeature[] audioFeatures = gson.fromJson(
                    String.valueOf(obj.getJSONArray("audio_features")), AudioFeature[].class);
            for (AudioFeature audioFeature : audioFeatures) {
                if (audioFeature == null) {
                    System.out.println("Null audiofeature found. Skipping track...");
                    continue;
                }
                trackDao.createAudioFeatures(audioFeature);
            }
        }
    }

    public String createNewPlaylist(String accessToken, String newPlaylistName, String newPlaylistDescription) throws IOException {
        JSONObject newPlaylistInfo = new JSONObject(Map.of("name", newPlaylistName,
                "description", newPlaylistDescription, "public", false));
        String newPlaylistUrl = "https://api.spotify.com/v1/me/playlists";
        JSONObject obj = SpotifyService.JsonPostRequest(accessToken, newPlaylistUrl, newPlaylistInfo);
        Playlist newPlaylist = gson.fromJson(String.valueOf(obj), Playlist.class);
        playlistDao.createPlaylist(newPlaylist, user.getId());
        return newPlaylist.getId();
    }

    public void addTrackItemsToPlaylist(String accessToken, String playlistId, List<String> tracksToAdd) throws IOException {
        int numberOfTimesToAddTracks = tracksToAdd.size() / 99 + 1;
        int j = 0;
        for (int i = 0; i < numberOfTimesToAddTracks; i++) {
            JSONArray trackListURIsArray = new JSONArray();
            for (; j < tracksToAdd.size(); j++) {
                trackListURIsArray.put("spotify:track:" + tracksToAdd.get(j));
                if (j % 99 == 98 && j != 0) {
                    j++;
                    break;
                }
            }
            JSONObject trackListUrisObj = new JSONObject(Map.of("uris", trackListURIsArray));
            String addTracksToPlaylistUrl = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
            SpotifyService.JsonPostRequest(accessToken, addTracksToPlaylistUrl, trackListUrisObj);
        }
        for (String trackId : tracksToAdd) {
            playlistDao.linkTrackToPlaylist(playlistId, trackId);
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
        if (playlistItem.isNull("track") ||
                playlistItem.getJSONObject("track").isNull("id") ||
                (playlistItem.keySet().contains("episode"))) {
            System.out.println("Null Track or Id found in playlist track parsing");
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
        if (trackItem == null || trackItem.isNull("id")) {
            System.out.println("Null Track or Id found in album track parsing");
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