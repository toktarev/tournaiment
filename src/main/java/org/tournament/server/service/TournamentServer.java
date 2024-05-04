package org.tournament.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tournament.server.config.TournamentServerConfig;
import org.tournament.server.handler.TournamentServerHandler;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentServer {

    private final TournamentServerConfig config;

    private final TournamentServerHandler tournamentServerHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        final var bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()
                )
        );

        bootstrap.setPipelineFactory(() -> {
            final var pipeLine = Channels.pipeline(tournamentServerHandler);
            pipeLine.addLast("decoder", new StringDecoder());
            pipeLine.addLast("encoder", new StringEncoder());
            return pipeLine;
        });

        bootstrap.bind(new InetSocketAddress(config.getPort()));
        log.info("Successfully starter tournament server port: {}", config.getPort());
    }
}
