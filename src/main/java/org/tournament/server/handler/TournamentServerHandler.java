package org.tournament.server.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelStateEvent;
import org.springframework.stereotype.Component;
import org.tournament.server.service.player.PlayerSessionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentServerHandler extends SimpleChannelHandler {

    private final PlayerSessionService playerService;

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        playerService.createSession(ctx.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        playerService.disposeSession(ctx.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        playerService.handleSessionEvent(ctx.getChannel(), e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error(e.getCause().getMessage(), e.getCause());
        playerService.disposeSession(ctx.getChannel());
    }
}
