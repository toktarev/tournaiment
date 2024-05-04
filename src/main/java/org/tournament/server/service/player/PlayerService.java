package org.tournament.server.service.player;

import org.tournament.server.service.player.entity.PlayerSession;

/**
 * Service which is responsible for new session creation and
 * looking for competitor for game's creation;
 */
public interface PlayerService {
    /**
     * Looking for competitor for the new session or pull
     * this session to the queue if now competitor found.
     *
     * @param playerSession - newly created player session.
     */
    void handlePlayerSession(final PlayerSession playerSession);
}
