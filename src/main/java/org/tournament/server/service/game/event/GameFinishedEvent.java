package org.tournament.server.service.game.event;

import org.springframework.context.ApplicationEvent;

public class GameFinishedEvent extends ApplicationEvent {
    public GameFinishedEvent(Object source) {
        super(source);
    }
}
