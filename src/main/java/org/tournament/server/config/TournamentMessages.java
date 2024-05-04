package org.tournament.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "tournament.server")
public class TournamentMessages {
    private String welcomeMessage;

    private String awaitingCompetitorMessage;

    private String competitorFoundMessage;

    private String welcomePlayerMessage;

    private String gameStartedMessage;

    private String pleaseEnterMessage;

    private String wrongStepMessage;

    private String drawMessage;

    private String wonMessage;

    private String looseMessage;

    private String goodByMessage;

    private String waitingCompetitorStep;

    private String exitWord;
}
