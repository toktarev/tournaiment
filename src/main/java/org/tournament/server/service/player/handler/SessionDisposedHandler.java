package org.tournament.server.service.player.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tournament.server.service.game.GameService;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;

@Component
@RequiredArgsConstructor
public class SessionDisposedHandler implements PlayerSessionStateHandler {

    private final GameService gameService;

    @Override
    public void handle(final PlayerSession playerSession,
                       final String message) {
        try {
            playerSession.setState(PlayerSessionState.SESSION_DISPOSED);

            final var game = gameService.getGame(playerSession);
            if (game != null) {
                gameService.disposeGameAndAnotherParticipant(game, playerSession);
            }
        } finally {
            playerSession.getChannel().close();
        }
    }

    @Override
    public PlayerSessionState getState() {
        return PlayerSessionState.SESSION_DISPOSED;
    }
}
