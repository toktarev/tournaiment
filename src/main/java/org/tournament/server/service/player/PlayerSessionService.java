package org.tournament.server.service.player;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

/**
 * Service which reads messages for client and manages
 * player session state.
 */
public interface PlayerSessionService {
    /**
     * Creates new physical session,
     *
     * @ aram channel - network channel.
     */
    void createSession(Channel channel);

    /**
     * Handles and dispatch events received from client.
     *
     * @param channel - network channel.
     * @param e       - event.
     */
    void handleSessionEvent(Channel channel, MessageEvent e);

    /**
     * Handles disposal session event.
     *
     * @param channel - network channel.
     */
    void disposeSession(Channel channel);
}
