package org.tournament.server.service.player.handler;

import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;

/**
 * Interface of handlers which process events
 * of player session depends on the current player session
 * state;
 */
public interface PlayerSessionStateHandler {
    /**
     * Handles message;
     *
     * @param playerSession - player session;
     * @param message       - message;
     */
    void handle(PlayerSession playerSession,
                String message);

    default void handle(PlayerSession playerSession) {
        handle(playerSession, "");
    }

    /**
     * Return corresponding state;
     *
     * @return - player session state;
     */
    PlayerSessionState getState();
}
