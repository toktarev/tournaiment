package org.tournament.server.service.player.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.player.PlayerService;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCreatedHandler implements PlayerSessionStateHandler {
    private final PlayerService playerService;

    private final TournamentMessages tournamentMessages;

    @Override
    public void handle(final PlayerSession playerSession,
                       final String name) {
        playerSession.createPlayer(name);
        log.info("Created player with name {}", name);
        playerSession.setState(PlayerSessionState.PLAYER_CREATED);
        playerSession.getChannel().write(tournamentMessages.getAwaitingCompetitorMessage());
        playerService.handlePlayerSession(playerSession);
    }

    @Override
    public PlayerSessionState getState() {
        return PlayerSessionState.SESSION_CREATED;
    }
}
