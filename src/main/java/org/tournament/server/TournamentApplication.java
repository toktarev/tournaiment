package org.tournament.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Tournament application.
 * This is multiplayer game server to play [ROCK, PAPER, SCISSORS].
 */
@SpringBootApplication
@EnableScheduling
public class TournamentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournamentApplication.class, args);
    }
}
