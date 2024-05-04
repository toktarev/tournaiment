package org.tournament.server.service.game.entity;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * Elements of the game;
 */
@RequiredArgsConstructor
public enum Step {
    ROCK(new Function<>() {
        @Override
        public GameResult apply(Step step) {
            return switch (step) {
                case ROCK -> GameResult.DRAW;
                case SCISSORS -> GameResult.WIN;
                case PAPER -> GameResult.LOOSE;
            };
        }
    }),
    SCISSORS(new Function<>() {
        @Override
        public GameResult apply(Step step) {
            return switch (step) {
                case SCISSORS -> GameResult.DRAW;
                case PAPER -> GameResult.WIN;
                case ROCK -> GameResult.LOOSE;
            };
        }
    }),
    PAPER(new Function<>() {
        @Override
        public GameResult apply(Step step) {
            return switch (step) {
                case PAPER -> GameResult.DRAW;
                case ROCK -> GameResult.WIN;
                case SCISSORS -> GameResult.LOOSE;
            };
        }
    });

    private final Function<Step, GameResult> comparator;

    public GameResult compare(Step anotherStep) {
        return this.comparator.apply(anotherStep);
    }
}
