package org.tournament.server.service.game.handler;

import org.tournament.server.service.game.entity.Game;
import org.tournament.server.service.game.entity.GameState;
import org.tournament.server.service.player.entity.PlayerSession;

/**
 * Handles game state handler;
 */
public interface GameStateHandler {
    /**
     * Handles messages for the certain game;
     *
     * @param game          - current game;
     * @param playerSession - current player session;
     * @param message       - message;
     */
    void handle(final Game game,
                final PlayerSession playerSession,
                final String message);

    /**
     * Corresponding game state;
     *
     * @return game state;
     */
    GameState getState();

}
