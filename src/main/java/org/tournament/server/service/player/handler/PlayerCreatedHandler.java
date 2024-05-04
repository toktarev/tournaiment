package org.tournament.server.service.player.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;
import org.tournament.server.service.player.event.PlayerSessionDisposedEvent;

@Component
@RequiredArgsConstructor
public class PlayerCreatedHandler implements PlayerSessionStateHandler {

    private final TournamentMessages tournamentMessages;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handle(final PlayerSession playerSession,
                       final String message) {
        if (message.equalsIgnoreCase(tournamentMessages.getExitWord())) {
            playerSession.getChannel().write(
                    String.format(
                            tournamentMessages.getGoodByMessage(),
                            playerSession.getPlayer().getName()
                    )
            );
            applicationEventPublisher.publishEvent(new PlayerSessionDisposedEvent(
                    playerSession
            ));
            return;
        }

        playerSession.getChannel().write(tournamentMessages.getAwaitingCompetitorMessage());
    }

    @Override
    public PlayerSessionState getState() {
        return PlayerSessionState.PLAYER_CREATED;
    }
}
