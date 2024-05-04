package org.tournament.server.service.player.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tournament.server.service.game.GameService;
import org.tournament.server.service.game.handler.GameHandlerRepository;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;

@Component
@RequiredArgsConstructor
public class SessionGameCreatedHandler implements PlayerSessionStateHandler {

    private final GameService gameService;

    private final GameHandlerRepository gameHandlerRepository;

    @Override
    public void handle(final PlayerSession playerSession,
                       final String message) {
        final var game = gameService.getGame(playerSession);
        gameHandlerRepository.getHandler(game.getGameState()).handle(
                game, playerSession, message
        );
    }

    @Override
    public PlayerSessionState getState() {
        return PlayerSessionState.GAME_CREATED;
    }
}
