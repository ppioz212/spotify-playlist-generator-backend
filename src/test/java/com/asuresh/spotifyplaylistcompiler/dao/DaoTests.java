package com.asuresh.spotifyplaylistcompiler.dao;

import com.asuresh.spotifyplaylistcompiler.jdbcdao.JdbcPlaylistDao;
import com.asuresh.spotifyplaylistcompiler.model.Playlist;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

public class DaoTests {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private JdbcPlaylistDao sut;
    @Before
    public void setup() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUsername("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/spotifyData");
        dataSource.setPassword("postgres1");
        sut = new JdbcPlaylistDao(dataSource);
    }

    @Test
    public void testGetPlaylists() {
        List<Playlist> playlists = sut.getPlaylists();
        for (Playlist playlist : playlists) {
            System.out.println("Name:" + playlist.getName() + " ID:" + playlist.getId());
        }
        Assert.assertEquals(49,playlists.size());
    }
}
