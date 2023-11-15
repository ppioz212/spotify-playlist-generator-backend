package com.asuresh.spotifyplaylistcompiler.controllers;

import com.asuresh.spotifyplaylistcompiler.jdbcdao.*;
import com.asuresh.spotifyplaylistcompiler.services.PlaylistDTO;
import com.asuresh.spotifyplaylistcompiler.services.SpotifyService;
import com.asuresh.spotifyplaylistcompiler.model.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.asuresh.spotifyplaylistcompiler.utils.MiscFunctions.*;

@RestController
public class MainController {
    private final JdbcAlbumDao albumDao;
    private final JdbcPlaylistDao playlistDao;
    private final JdbcTrackDao trackDao;
    private final JdbcUserDao userDao;
    private final Gson gson;

    MainController(JdbcAlbumDao jdbcAlbumDao, JdbcPlaylistDao jdbcPlaylistDao, JdbcTrackDao jdbcTrackDao, JdbcUserDao jdbcUserDao) {
        this.albumDao = jdbcAlbumDao;
        this.playlistDao = jdbcPlaylistDao;
        this.trackDao = jdbcTrackDao;
        this.userDao = jdbcUserDao;
        gson = new Gson();
    }

    @PostMapping("/generateNewPlaylist")
    public String generateNewPlaylist(
            @RequestBody PlaylistDTO playlistDTO,
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("UserId") String userId) throws IOException {
        if (playlistDTO.getNameOfPlaylist().equals("test")) {
            return "38mJZ8lgs9au7jSqbv6EJZ";
        }
        String newPlaylistId = createNewPlaylist(accessToken, playlistDTO.getNameOfPlaylist(), "", userId);
        List<String> tracksToAdd = trackDao.getTrackIds(playlistDTO.getAlbumsToAdd(), playlistDTO.getPlaylistsToAdd(),
                playlistDTO.isAddLikedSongs(), playlistDTO.getMinTempo(), playlistDTO.getMaxTempo(), userId);
        System.out.println(tracksToAdd.size());
        addTrackItemsToPlaylist(accessToken, newPlaylistId, tracksToAdd, userId);
        return newPlaylistId;
    }

    @GetMapping("/getPlaylists")
    public List<Playlist> getPlaylists(@RequestHeader("Authorization") String accessToken,
                                       @RequestHeader("UserId") String userId) throws IOException {
        if (userDao.wasDataPreviouslyPulled(TableType.PLAYLIST, userId)) {
            System.out.println("Playlists found");
            return playlistDao.getPlaylists(userId);
        }
        String playlistUrl = "https://api.spotify.com/v1/me/playlists?limit=50";
        while (playlistUrl != null) {
            JSONObject obj = SpotifyService.jsonGetRequest(accessToken, playlistUrl);
            Playlist[] playlists = gson.fromJson(
                    String.valueOf(obj.getJSONArray("items")), Playlist[].class);
            for (Playlist playlist : playlists) {
                playlist.setUserId(userId);
                playlistDao.createPlaylist(playlist);
            }
            playlistUrl = checkIfNextURLAvailable(obj);
        }
        userDao.updateDataPulled(TableType.PLAYLIST, true, userId);
        return playlistDao.getPlaylists(userId);
    }

    @GetMapping("/getAlbums")
    public List<Album> getAlbums(@RequestHeader("Authorization") String accessToken,
                                 @RequestHeader("UserId") String userId) throws IOException {
        if (userDao.wasDataPreviouslyPulled(TableType.ALBUM, userId)) {
            System.out.println("Albums found");
            return albumDao.getAlbums(userId);
        }
        String albumUrl = "https://api.spotify.com/v1/me/albums?limit=50";
        while (albumUrl != null) {
            JSONObject obj = SpotifyService.jsonGetRequest(accessToken, albumUrl);
            JSONArray albumItems = obj.getJSONArray("items");
            for (int i = 0; i < albumItems.length(); i++) {
                Album album = getAlbumFromJson(albumItems.getJSONObject(i).getJSONObject("album"), userId);
                albumDao.createAlbum(album);
            }
            albumUrl = checkIfNextURLAvailable(obj);
        }
        userDao.updateDataPulled(TableType.ALBUM, true, userId);
        return albumDao.getAlbums(userId);
    }

    @GetMapping("/tracks/max")
    public double getMaxTempoOfTracks(@RequestHeader("UserId") String userId) {
        return trackDao.getMaxTempoOfTracks(userId);
    }

    @GetMapping("/tracks/min")
    public double getMinTempoOfTracks(@RequestHeader("UserId") String userId) {
        return trackDao.getMinTempoOfTracks(userId);
    }

    @GetMapping("/compileTracks")
    public void createAllTracks(@RequestHeader("Authorization") String accessToken,
                                @RequestHeader("UserId") String userId) throws IOException {
        if (userDao.wasDataPreviouslyPulled(TableType.TRACK, userId)) {
            System.out.println("Track data found");
            return;
        }
        System.out.println("Tracks not found");

        List<String> albumIds = albumDao.getAlbumIds(userId);
        List<String> playlistIds = playlistDao.getPlaylistIds(userId);

        System.out.println("Compiling Album tracks");
        compileAlbumTrackIds(albumIds, accessToken, userId);
        System.out.println("Album tracks compiled");

        System.out.println("Compiling Playlist tracks");
        compilePlaylistTrackIds(playlistIds, accessToken, userId);
        System.out.println("Playlist tracks compiled");

        System.out.println("Compiling Saved tracks");
        compileUserSavedSongIds(accessToken, userId);
        System.out.println("Saved tracks compiled");

        System.out.println("Compiling audio features");
        compileTrackFeatures(accessToken, userId);
        System.out.println("Audio features compiled");

        userDao.updateDataPulled(TableType.TRACK, true, userId);
        userDao.updateLastUpdatedTimestamp(userId);
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

    public void compilePlaylistTrackIds(List<String> playlistIds, String accessToken, String userId) throws IOException {
        for (String playlistID : playlistIds) {
            String playlistTracksUrl = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks?limit=50";
            while (playlistTracksUrl != null) {
                JSONObject obj = SpotifyService.jsonGetRequest(accessToken, playlistTracksUrl);
                JSONArray playlistItems = obj.getJSONArray("items");
                for (int i = 0; i < playlistItems.length(); i++) {
                    Track track = getTrackFromPlaylistJson(playlistItems.getJSONObject(i), false, userId);
                    if (track == null) {
                        continue;
                    }
                    trackDao.createTrack(track);
                    playlistDao.linkTrackToPlaylist(playlistID, track.getId(), userId);
                }
                playlistTracksUrl = checkIfNextURLAvailable(obj);
            }
        }
    }

    public void compileAlbumTrackIds(List<String> albumIds, String accessToken, String userId) throws IOException {
        for (String albumID : albumIds) {
            String albumTracksUrl = "https://api.spotify.com/v1/albums/" + albumID + "/tracks?limit=50";
            while (albumTracksUrl != null) {
                JSONObject obj = SpotifyService.jsonGetRequest(accessToken, albumTracksUrl);
                JSONArray albumItems = obj.getJSONArray("items");
                for (int i = 0; i < albumItems.length(); i++) {
                    Track track = getTrackFromAlbumJson(albumItems.getJSONObject(i), false, userId);
                    if (track == null) {
                        continue;
                    }
                    trackDao.createTrack(track);
                    albumDao.linkTrackToAlbum(albumID, track.getId(), userId);
                }
                albumTracksUrl = checkIfNextURLAvailable(obj);
            }
        }
    }

    public void compileUserSavedSongIds(String accessToken, String userId) throws IOException {
        String savedSongsUrl = "https://api.spotify.com/v1/me/tracks?limit=50";
        while (savedSongsUrl != null) {
            JSONObject obj = SpotifyService.jsonGetRequest(accessToken, savedSongsUrl);
            JSONArray savedSongsItems = obj.getJSONArray("items");
            for (int i = 0; i < savedSongsItems.length(); i++) {
                Track track = getTrackFromPlaylistJson(savedSongsItems.getJSONObject(i), true, userId);
                if (track == null) {
                    continue;
                }
                trackDao.createTrack(track);
            }
            savedSongsUrl = checkIfNextURLAvailable(obj);
        }
    }


    public void compileTrackFeatures(String accessToken, String userId) throws IOException {
        List<String> allTrackIds = trackDao.getTrackIds(userId);
        String[] queryStringParams = compileAudioFeatureQSParams(allTrackIds);
        for (String queryStringParam : queryStringParams) {
            String trackFeaturesUrl = "https://api.spotify.com/v1/audio-features?ids=" + queryStringParam;
            JSONObject obj = SpotifyService.jsonGetRequest(accessToken, trackFeaturesUrl);
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

    public String createNewPlaylist(String accessToken, String newPlaylistName, String newPlaylistDescription, String userId) throws IOException {
        JSONObject newPlaylistInfo = new JSONObject(Map.of("name", newPlaylistName,
                "description", newPlaylistDescription, "public", false));
        String newPlaylistUrl = "https://api.spotify.com/v1/me/playlists";
        JSONObject obj = SpotifyService.jsonPostRequest(accessToken, newPlaylistUrl, newPlaylistInfo);
        Playlist newPlaylist = gson.fromJson(String.valueOf(obj), Playlist.class);
        newPlaylist.setUserId(userId);
        playlistDao.createPlaylist(newPlaylist);
        return newPlaylist.getId();
    }

    public void addTrackItemsToPlaylist(String accessToken, String playlistId, List<String> tracksToAdd, String userId) throws IOException {
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
            SpotifyService.jsonPostRequest(accessToken, addTracksToPlaylistUrl, trackListUrisObj);
        }
        for (String trackId : tracksToAdd) {
            playlistDao.linkTrackToPlaylist(playlistId, trackId, userId);
        }
    }

    private String[] compileAudioFeatureQSParams(List<String> allTrackIds) {
        int numberOfTimesToHitFeaturesEndpoint = allTrackIds.size() / 100 + 1;
        String[] qsParams = new String[numberOfTimesToHitFeaturesEndpoint];
        int j = 0;
        for (int i = 0; i < numberOfTimesToHitFeaturesEndpoint; i++) {
            StringBuilder sb_ids = new StringBuilder();
            for (; j < allTrackIds.size(); j++) {
                if (j % 100 == 0) {
                    sb_ids.append(allTrackIds.get(j));
                } else {
                    sb_ids.append("%2C").append(allTrackIds.get(j));
                }
                if (j % 100 == 99 && j != 0) {
                    j++;
                    break;
                }
            }
            qsParams[i] = sb_ids.toString();
        }
        return qsParams;
    }

    public Album getAlbumFromJson(JSONObject albumData, String userId) {
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

    public Track getTrackFromPlaylistJson(JSONObject playlistItem, boolean isLikedSong, String userId) {
        if (playlistItem.isNull("track") ||
                playlistItem.getJSONObject("track").isNull("id") ||
                (playlistItem.keySet().contains("episode"))) {
            System.out.println("Null Track or Id found in playlist track parsing");
            return null;
        }
        Track track = new Track();
        track.setId(playlistItem.getJSONObject("track").getString("id"));
        track.setName(playlistItem.getJSONObject("track").getString("name"));
        track.setUserId(userId);
        track.setLikedSong(isLikedSong);
        return track;
    }

    public Track getTrackFromAlbumJson(JSONObject trackItem, boolean isLikedSong, String userId) {
        if (trackItem == null || trackItem.isNull("id")) {
            System.out.println("Null Track or Id found in album track parsing");
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