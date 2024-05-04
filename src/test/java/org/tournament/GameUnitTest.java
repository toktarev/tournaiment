package org.tournament;

import org.jboss.netty.channel.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.game.entity.Game;
import org.tournament.server.service.game.entity.Participant;
import org.tournament.server.service.game.entity.Step;
import org.tournament.server.service.game.handler.GameCreatedHandler;
import org.tournament.server.service.player.entity.Player;
import org.tournament.server.service.player.entity.PlayerSession;

public class GameUnitTest {
    private static final String PLAYER_1 = "player1";
    private static final String PLAYER_2 = "player2";
    private static final String WIN_MESSAGE = "You won. %s entered %s";
    private static final String LOOSE_MESSAGE = "You loose. %s entered %s";
    private static final String DRAW_MESSAGE = "Draw. %s entered %s";
    private static final String WRONG_STEP_MESSAGE = "You entered wrong step: %s";
    private static final String ENTER_MESSAGE = "Please enter: [ROCK, PAPER, SCISSORS]";

    private ApplicationEventPublisher applicationEventPublisher;
    private TournamentMessages tournamentMessages;
    private Game game;
    private PlayerSession playerSession1;
    private PlayerSession playerSession2;
    private Participant participant1;
    private Participant participant2;

    private Channel channel1;

    private Channel channel2;

    @BeforeEach
    public void before() {
        game = Mockito.mock(Game.class);
        playerSession1 = Mockito.mock(PlayerSession.class);
        playerSession2 = Mockito.mock(PlayerSession.class);
        applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        tournamentMessages = Mockito.mock(TournamentMessages.class);
        channel1 = Mockito.mock(Channel.class);
        channel2 = Mockito.mock(Channel.class);
        participant1 = new Participant(playerSession1);
        participant2 = new Participant(playerSession2);
        Mockito.doReturn(participant1).when(game).getCurrentPlayer(playerSession1);
        Mockito.doReturn(participant2).when(game).getCompetitor(playerSession1);
        Mockito.doReturn(channel1).when(playerSession1).getChannel();
        Mockito.doReturn(channel2).when(playerSession2).getChannel();
        Mockito.doReturn(new Player(PLAYER_1)).when(playerSession1).getPlayer();
        Mockito.doReturn(new Player(PLAYER_2)).when(playerSession2).getPlayer();
        Mockito.doReturn(WIN_MESSAGE).when(tournamentMessages).getWonMessage();
        Mockito.doReturn(LOOSE_MESSAGE).when(tournamentMessages).getLooseMessage();
        Mockito.doReturn(DRAW_MESSAGE).when(tournamentMessages).getDrawMessage();
        Mockito.doReturn(WRONG_STEP_MESSAGE).when(tournamentMessages).getWrongStepMessage();
        Mockito.doReturn(ENTER_MESSAGE).when(tournamentMessages).getPleaseEnterMessage();
    }

    @Test
    public void testWinGame() {
        final var winnerStep = Step.PAPER;
        final var looserStep = Step.ROCK;
        participant1.setStep(null);
        participant2.setStep(looserStep);
        Mockito.doAnswer(invocationOnMock -> {
            participant1.setStep(winnerStep);
            return null;
        }).when(game).acceptStep(playerSession1, winnerStep);
        final var handler = new GameCreatedHandler(
                tournamentMessages,
                applicationEventPublisher
        );
        handler.handle(
                game, playerSession1, winnerStep.name()
        );
        Mockito.verify(game).acceptStep(playerSession1, winnerStep);
        Mockito.verify(channel1).write(
                String.format(
                        WIN_MESSAGE,
                        participant2.getPlayerSession().getPlayer().getName(),
                        looserStep.name()
                )
        );
    }

    @Test
    public void testDrawGame() {
        final var step = Step.PAPER;
        participant1.setStep(null);
        participant2.setStep(step);
        Mockito.doAnswer(invocationOnMock -> {
            participant1.setStep(step);
            return null;
        }).when(game).acceptStep(playerSession1, step);
        final var handler = new GameCreatedHandler(
                tournamentMessages,
                applicationEventPublisher
        );
        handler.handle(
                game, playerSession1, step.name()
        );
        Mockito.verify(game).acceptStep(playerSession1, step);
        Mockito.verify(channel1).write(
                String.format(
                        DRAW_MESSAGE,
                        participant2.getPlayerSession().getPlayer().getName(),
                        step.name()
                )
        );
    }

    @Test
    public void testInvalidStep() {
        final String wrongStep = "WRONG_STEP";
        participant1.setStep(null);
        final var handler = new GameCreatedHandler(
                tournamentMessages,
                applicationEventPublisher
        );
        handler.handle(
                game, playerSession1, wrongStep
        );
        Mockito.verify(channel1).write(
                String.format(WRONG_STEP_MESSAGE, wrongStep)
        );
        Mockito.verify(channel1).write(
                ENTER_MESSAGE
        );
    }
}
