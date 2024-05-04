package org.tournament.server.service.player;

import lombok.RequiredArgsConstructor;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tournament.server.config.TournamentMessages;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tournament.server.service.player.entity.PlayerSession;
import org.tournament.server.service.player.entity.PlayerSessionState;
import org.tournament.server.service.player.event.PlayerSessionDisposedEvent;
import org.tournament.server.service.player.handler.PlayerHandlerRepository;

@Service
@RequiredArgsConstructor
public class DefaultPlayerSessionService implements PlayerSessionService {
    private static final byte[] CTRL_C_BYTES = new byte[]{
            -1, -12, -1, -3, 6
    };

    private final TournamentMessages messages;

    private final PlayerHandlerRepository playerHandlerRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Map<Integer, PlayerSession> playerSessions = new ConcurrentHashMap<>();

    private static String readMessage(MessageEvent e) {
        if (e.getMessage() instanceof ChannelBuffer buffer) {
            return buffer.toString(Charset.defaultCharset()).trim();
        }

        return e.getMessage().toString();
    }

    @Override
    public void createSession(Channel channel) {
        playerSessions.put(channel.getId(), new PlayerSession(channel));
        channel.write(messages.getWelcomeMessage());
    }

    @Override
    public void handleSessionEvent(Channel channel, MessageEvent e) {
        final var playerSession = playerSessions.get(channel.getId());

        if (isCtrlC(e)) {
            applicationEventPublisher.publishEvent(
                    new PlayerSessionDisposedEvent(playerSession)
            );
            return;
        }

        playerHandlerRepository.getHandler(playerSession.getState()).handle(
                playerSession, readMessage(e)
        );
    }

    private static boolean isCtrlC(final MessageEvent e) {
        if (e.getMessage() instanceof ChannelBuffer buffer) {
            return Arrays.equals(buffer.array(), CTRL_C_BYTES);
        }

        return false;
    }

    @Override
    public void disposeSession(final Channel channel) {
        final var session = playerSessions.remove(channel.getId());
        if (session != null) {
            playerHandlerRepository.getHandler(PlayerSessionState.SESSION_DISPOSED).handle(
                    session
            );
        }
    }

    @EventListener(PlayerSessionDisposedEvent.class)
    public void disposePlayerSession(PlayerSessionDisposedEvent event) {
        if (event.getSource() instanceof PlayerSession session) {
            disposeSession(session.getChannel());
        }
    }
}
