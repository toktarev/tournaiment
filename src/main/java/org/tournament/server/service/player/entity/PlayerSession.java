package org.tournament.server.service.player.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jboss.netty.channel.Channel;

import java.util.Objects;

@Data
@RequiredArgsConstructor
public class PlayerSession {
    private final Channel channel;

    private volatile PlayerSessionState state = PlayerSessionState.SESSION_CREATED;

    private volatile Player player;

    public void createPlayer(String name) {
        player = new Player(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return Objects.equals(channel.getId(), that.channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel.getId());
    }
}
