package com.epn.casino.model.game;

import java.time.LocalDateTime;

/**
 * Representa el resultado inmutable de una partida.
 */
public record GameResult(
        boolean isWinner,
        double multiplier,
        String description,
        LocalDateTime timestamp
) {
    public GameResult(boolean isWinner, double multiplier, String description) {
        this(isWinner, multiplier, description, LocalDateTime.now());
    }
}