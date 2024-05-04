package org.tournament.server.service.player.handler;

import org.springframework.stereotype.Component;
import org.tournament.server.service.player.entity.PlayerSessionState;
import java.util.EnumMap;
import java.util.List;

@Component
public class PlayerHandlerRepository {
    private final EnumMap<PlayerSessionState, PlayerSessionStateHandler> stateHandlerEnumMap =
            new EnumMap<>(PlayerSessionState.class);

    public PlayerHandlerRepository(final List<PlayerSessionStateHandler> handlers) {
        handlers.forEach(
                playerStateHandler ->
                        stateHandlerEnumMap.put(playerStateHandler.getState(), playerStateHandler)
        );
    }

    public PlayerSessionStateHandler getHandler(final PlayerSessionState state) {
        return stateHandlerEnumMap.get(state);
    }
}
