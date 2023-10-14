package com.asuresh.spotifyplaylistcompiler;


import com.asuresh.spotifyplaylistcompiler.model.SpotifyAlbum;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JDBCtest {

    public static void main(String[] args) {
        try (BasicDataSource dataSource = new BasicDataSource()) {

            dataSource.setUsername("postgres");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/spotifyData");
            dataSource.setPassword("postgres1");
            SpotifyAlbum albumObject = new SpotifyAlbum();
            albumObject.setId("10q8wXsKGU1IFt6JJg5qGm");
            albumObject.setName("Thia Litourgia");
            albumObject.setArtists("Choir Of Vatopedi Fathers");

            System.out.println("The added Album id is: " + create_album(dataSource,albumObject));

        } catch (Exception e) {
            System.out.println("You poop'd your pants");
        }

    }

    public static String create_album(DataSource dataSource, SpotifyAlbum album) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "INSERT INTO album (id, name, artists) VALUES (?, ?, ?) RETURNING name;";
        return jdbcTemplate.queryForObject(sql, String.class, album.getId(), album.getName(), album.getArtists());
    }
}