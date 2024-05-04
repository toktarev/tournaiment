package org.tournament.server.service.player.event;

import org.springframework.context.ApplicationEvent;
import org.tournament.server.service.player.entity.PlayerSession;

public class PlayerSessionDisposedEvent extends ApplicationEvent {
    public PlayerSessionDisposedEvent(PlayerSession session) {
        super(session);
    }
}
