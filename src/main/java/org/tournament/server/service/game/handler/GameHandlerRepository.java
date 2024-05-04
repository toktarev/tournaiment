package org.tournament.server.service.game.handler;

import org.springframework.stereotype.Component;
import org.tournament.server.service.game.entity.GameState;

import java.util.EnumMap;
import java.util.List;

@Component
public class GameHandlerRepository {
    private final EnumMap<GameState, GameStateHandler> stateHandlerEnumMap =
            new EnumMap<>(GameState.class);

    public GameHandlerRepository(final List<GameStateHandler> handlers) {
        handlers.forEach(
                playerStateHandler ->
                        stateHandlerEnumMap.put(playerStateHandler.getState(), playerStateHandler)
        );
    }

    public GameStateHandler getHandler(final GameState state) {
        return stateHandlerEnumMap.get(state);
    }

}
