package org.tournament.server.service.game.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.tournament.server.service.player.entity.PlayerSession;

@Data
@RequiredArgsConstructor
public class Participant {
    private final PlayerSession playerSession;

    private volatile Step step;

    public void handle(final Step step) {
        if (this.step == null) {
            this.step = step;
        }
    }

    public GameResult compete(Participant participant) {
        if ((step == null) || (participant.getStep() == null)) {
            return GameResult.UNDEFINED;
        }

        return step.compare(participant.step);
    }

    public void reset() {
        this.step = null;
    }
}
