package com.asuresh.spotifyplaylistcompiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SpotifyApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpotifyApplication.class, args);
    }
}