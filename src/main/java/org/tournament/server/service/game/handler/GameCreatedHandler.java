package org.tournament.server.service.game.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.game.entity.Game;
import org.tournament.server.service.game.entity.GameState;
import org.tournament.server.service.game.entity.Participant;
import org.tournament.server.service.game.entity.Step;
import org.tournament.server.service.game.event.GameFinishedEvent;
import org.tournament.server.service.player.entity.PlayerSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameCreatedHandler implements GameStateHandler {

    private final TournamentMessages tournamentMessages;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handle(final Game game,
                       final PlayerSession playerSession,
                       final String step) {
        final var currentPlayer = game.getCurrentPlayer(playerSession);
        final var competitor = game.getCompetitor(playerSession);

        if (currentPlayer.getStep() != null) {
            playerSession.getChannel().write(
                    String.format(
                            tournamentMessages.getWaitingCompetitorStep(),
                            competitor.getPlayerSession().getPlayer().getName()
                    )
            );
            return;
        }

        if (isValidStep(step)) {
            handleValidStep(game, playerSession, step, currentPlayer, competitor);
            return;
        }

        handleInvalidStep(playerSession, step);
    }

    private void handleInvalidStep(PlayerSession playerSession, String step) {
        playerSession.getChannel().write(
                String.format(tournamentMessages.getWrongStepMessage(), step)
        );
        playerSession.getChannel().write(tournamentMessages.getPleaseEnterMessage());
    }

    private void handleValidStep(Game game,
                                 PlayerSession playerSession,
                                 String step,
                                 Participant currentPlayer,
                                 Participant competitor) {
        final var stepEnum = Step.valueOf(step.toUpperCase());
        game.acceptStep(playerSession, stepEnum);
        final var result = currentPlayer.compete(competitor);
        switch (result) {
            case WIN -> handleGameFinished(
                    game,
                    currentPlayer,
                    competitor
            );
            case LOOSE -> handleGameFinished(
                    game,
                    competitor,
                    currentPlayer
            );
            case DRAW -> handleDraw(
                    game,
                    competitor,
                    currentPlayer
            );
            case UNDEFINED -> playerSession.getChannel().write(
                    String.format(
                            tournamentMessages.getWaitingCompetitorStep(),
                            competitor.getPlayerSession().getPlayer().getName()
                    )
            );
        }
    }

    private void handleGameFinished(final Game game,
                                    final Participant winner,
                                    final Participant looser) {
        log.info("Game finished winner - {}, looser - {}",
                winner.getPlayerSession().getPlayer().getName(),
                looser.getPlayerSession().getPlayer().getName()
        );
        notifyPlayer(winner.getPlayerSession(), String.format(
                tournamentMessages.getWonMessage(),
                looser.getPlayerSession().getPlayer().getName(),
                looser.getStep().name()
        ));
        notifyPlayer(looser.getPlayerSession(), String.format(
                tournamentMessages.getLooseMessage(),
                winner.getPlayerSession().getPlayer().getName(),
                winner.getStep().name()
        ));
        applicationEventPublisher.publishEvent(new GameFinishedEvent(game));
    }

    private void handleDraw(final Game game,
                            final Participant participant1,
                            final Participant participant2) {
        log.info("Game finished with draw player1 - {}, player2 - {}",
                participant1.getPlayerSession().getPlayer().getName(),
                participant2.getPlayerSession().getPlayer().getName()
        );
        notifyPlayer(participant1.getPlayerSession(), String.format(
                tournamentMessages.getDrawMessage(),
                participant2.getPlayerSession().getPlayer().getName(),
                participant2.getStep().name()
        ));
        notifyPlayer(participant2.getPlayerSession(), String.format(
                tournamentMessages.getDrawMessage(),
                participant1.getPlayerSession().getPlayer().getName(),
                participant1.getStep().name()
        ));
        notifyPlayer(
                participant1.getPlayerSession(),
                tournamentMessages.getPleaseEnterMessage());
        notifyPlayer(
                participant2.getPlayerSession(),
                tournamentMessages.getPleaseEnterMessage());
        game.reset();
    }

    @Override
    public GameState getState() {
        return GameState.GAME_CREATED;
    }

    private static void notifyPlayer(final PlayerSession playerSession,
                                     final String message) {
        playerSession.getChannel().write(message);
    }

    private static boolean isValidStep(String step) {
        for (Step c : Step.values()) {
            if (c.name().equals(step)) {
                return true;
            }
        }

        return false;
    }
}
