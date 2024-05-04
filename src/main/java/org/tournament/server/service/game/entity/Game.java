package org.tournament.server.service.game.entity;

import lombok.Data;
import org.tournament.server.service.player.entity.PlayerSession;

@Data
public class Game {
    private final Participant participant1;

    private final Participant participant2;

    private volatile GameState gameState = GameState.GAME_CREATED;

    public Game(PlayerSession playerSession1,
                PlayerSession playerSession2) {
        this.participant1 = new Participant(playerSession1);
        this.participant2 = new Participant(playerSession2);
    }

    public void acceptStep(final PlayerSession playerSession,
                           final Step step) {
        if (playerSession == participant1.getPlayerSession()) {
            participant1.handle(step);
        }
        if (playerSession == participant2.getPlayerSession()) {
            participant2.handle(step);
        }
    }

    public Participant getCompetitor(final PlayerSession playerSession) {
        if (playerSession == participant1.getPlayerSession()) {
            return participant2;
        }

        return participant1;
    }

    public Participant getCurrentPlayer(PlayerSession playerSession) {
        if (playerSession == participant1.getPlayerSession()) {
            return participant1;
        }

        return participant2;
    }

    public void reset() {
        participant1.reset();
        participant2.reset();
    }
}
