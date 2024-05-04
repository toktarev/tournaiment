package org.tournament;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.tournament.server.config.TournamentMessages;
import org.tournament.server.service.game.GameService;
import org.tournament.server.service.game.entity.Game;
import org.tournament.server.service.game.entity.GameState;
import org.tournament.server.service.game.handler.GameHandlerRepository;
import org.tournament.server.service.game.handler.GameStateHandler;
import org.tournament.server.service.player.PlayerService;
import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;
import org.tournament.server.service.player.handler.PlayerCreatedHandler;
import org.tournament.server.service.player.handler.SessionCreatedHandler;
import org.tournament.server.service.player.handler.SessionDisposedHandler;
import org.tournament.server.service.player.handler.SessionGameCreatedHandler;

public class PlayerSessionUnitTest {
    private static final String PLAYER_NAME = "test";

    private static final String MESSAGE = "message";

    private PlayerService playerService;
    private TournamentMessages tournamentMessages;
    ApplicationEventPublisher applicationEventPublisher;
    private PlayerSession playerSession;
    private Channel channel;
    private Game game;
    private GameService gameService;
    private GameHandlerRepository gameHandlerRepository;
    private GameStateHandler gameStateHandler;

    @BeforeEach
    public void before() {
        game = Mockito.mock(Game.class);
        applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        playerService = Mockito.mock(PlayerService.class);
        tournamentMessages = Mockito.mock(TournamentMessages.class);
        playerSession = Mockito.mock(PlayerSession.class);
        channel = Mockito.mock(Channel.class);
        gameService = Mockito.mock(GameService.class);
        gameHandlerRepository = Mockito.mock(GameHandlerRepository.class);
        gameStateHandler = Mockito.mock(GameStateHandler.class);

        Mockito.doReturn(channel).when(playerSession).getChannel();
        Mockito.doReturn(MESSAGE).when(tournamentMessages).getAwaitingCompetitorMessage();
        Mockito.doNothing().when(playerSession).createPlayer(PLAYER_NAME);
        Mockito.doNothing().when(playerSession).setState(PlayerSessionState.PLAYER_CREATED);
        Mockito.doReturn(Mockito.mock(ChannelFuture.class)).when(channel).write(MESSAGE);
        Mockito.doNothing().when(playerService).handlePlayerSession(playerSession);

        Mockito.doReturn(GameState.GAME_CREATED).when(game).getGameState();
        Mockito.doReturn(game).when(gameService).getGame(playerSession);
        Mockito.doReturn(gameStateHandler)
                .when(gameHandlerRepository)
                .getHandler(GameState.GAME_CREATED);
        Mockito.doNothing().when(gameStateHandler).handle(game, playerSession, MESSAGE);
    }

    @Test
    public void testSessionCreated() {
        final var handler = new SessionCreatedHandler(
                playerService,
                tournamentMessages
        );
        handler.handle(playerSession, PLAYER_NAME);
        Mockito.verify(playerSession).createPlayer(PLAYER_NAME);
        Mockito.verify(playerSession).setState(PlayerSessionState.PLAYER_CREATED);
        Mockito.verify(channel).write(MESSAGE);
        Mockito.verify(playerService).handlePlayerSession(playerSession);
    }

    @Test
    public void testPlayerCreated() {
        final var handler = new PlayerCreatedHandler(tournamentMessages, applicationEventPublisher);
        handler.handle(playerSession, PLAYER_NAME);
        Mockito.verify(channel).write(MESSAGE);
    }

    @Test
    public void testGameCreated() {
        final var handler = new SessionGameCreatedHandler(
                gameService, gameHandlerRepository
        );
        handler.handle(playerSession, MESSAGE);
        Mockito.verify(gameService).getGame(playerSession);
        Mockito.verify(gameHandlerRepository).getHandler(GameState.GAME_CREATED);
        Mockito.verify(gameStateHandler).handle(
                game, playerSession, MESSAGE
        );
    }

    @Test
    public void testSessionDisposed() {
        final var handler = new SessionDisposedHandler(
                gameService
        );
        handler.handle(playerSession, MESSAGE);
        Mockito.verify(gameService).getGame(playerSession);
        Mockito.verify(gameService).disposeGameAndAnotherParticipant(
                game, playerSession
        );
        Mockito.verify(channel).close();
    }
}
