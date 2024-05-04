package org.tournament.server.service.game;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.game.entity.Game;
import org.tournament.server.service.game.event.GameFinishedEvent;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;
import org.tournament.server.service.player.event.PlayerSessionDisposedEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameService {

    private final TournamentMessages messages;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Map<PlayerSession, Game> playerSessionGameMap = new ConcurrentHashMap<>();

    public void createGame(PlayerSession playerSession1,
                           PlayerSession playerSession2) {
        final var game = new Game(playerSession1, playerSession2);
        playerSessionGameMap.put(playerSession1, game);
        playerSessionGameMap.put(playerSession2, game);
        notifyPlayers(playerSession1, playerSession2);
        notifyPlayers(playerSession2, playerSession1);
        setState(playerSession1);
        setState(playerSession2);
    }

    private static void setState(PlayerSession playerSession) {
        playerSession.setState(PlayerSessionState.GAME_CREATED);
    }

    private void notifyPlayers(PlayerSession playerSession1,
                               PlayerSession playerSession2) {
        playerSession1.getChannel().write(String.format(
                messages.getCompetitorFoundMessage(),
                playerSession2.getPlayer().getName())
        );
        playerSession1.getChannel().write(messages.getGameStartedMessage());
        playerSession1.getChannel().write(messages.getPleaseEnterMessage());
    }

    public Game getGame(PlayerSession session) {
        return playerSessionGameMap.get(session);
    }

    @EventListener(GameFinishedEvent.class)
    public void disposeGame(GameFinishedEvent event) {
        if (event.getSource() instanceof Game game) {
            playerSessionGameMap.remove(game.getParticipant1().getPlayerSession());
            playerSessionGameMap.remove(game.getParticipant2().getPlayerSession());
            applicationEventPublisher.publishEvent(new PlayerSessionDisposedEvent(
                    game.getParticipant1().getPlayerSession()
            ));
            applicationEventPublisher.publishEvent(new PlayerSessionDisposedEvent(
                    game.getParticipant2().getPlayerSession()
            ));
        }
    }

    public void disposeGameAndAnotherParticipant(final Game game,
                                                 final PlayerSession currentSession) {
        playerSessionGameMap.remove(game.getParticipant1().getPlayerSession());
        playerSessionGameMap.remove(game.getParticipant2().getPlayerSession());

        if (currentSession != game.getParticipant1().getPlayerSession()) {
            applicationEventPublisher.publishEvent(new PlayerSessionDisposedEvent(
                    game.getParticipant1().getPlayerSession()
            ));
        }

        if (currentSession != game.getParticipant2().getPlayerSession()) {
            applicationEventPublisher.publishEvent(new PlayerSessionDisposedEvent(
                    game.getParticipant2().getPlayerSession()
            ));
        }
    }
}
