package org.tournament.server.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tournament.server.service.game.GameService;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

@Component
@RequiredArgsConstructor
public class DefaultPlayerService implements PlayerService {
    public static final int SCHEDULING_DELAY = 1000;

    public static final int NANOS_DELAY = 100;

    private final Queue<PlayerSession> awaitingPlayerSessions = new ConcurrentLinkedQueue<>();

    private final GameService gameService;

    @Override
    public void handlePlayerSession(final PlayerSession playerSession) {
        final var competitor = awaitingPlayerSessions.poll();

        if (isInvalidSession(competitor)) {
            awaitingPlayerSessions.add(playerSession);
        } else {
            gameService.createGame(competitor, playerSession);
        }
    }

    /**
     * Scheduled task which is looking for competitors for
     * puled sessions and creates corresponding game.
     */
    @Scheduled(fixedDelay = SCHEDULING_DELAY)
    public void findCompetitor() {
        do {
            final var competitor1 = awaitingPlayerSessions.poll();

            if (isInvalidSession(competitor1)) {
                return;
            }

            final var competitor2 = awaitingPlayerSessions.poll();

            if (isInvalidSession(competitor2)) {
                awaitingPlayerSessions.add(competitor1);
                return;
            }

            gameService.createGame(competitor1, competitor2);
            LockSupport.parkNanos(NANOS_DELAY);
        } while (true);
    }

    private static boolean isInvalidSession(PlayerSession competitor) {
        return (competitor == null) || (competitor.getState() == PlayerSessionState.SESSION_DISPOSED);
    }
}
