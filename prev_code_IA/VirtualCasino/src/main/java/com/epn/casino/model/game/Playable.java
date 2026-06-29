package com.epn.casino.model.game;

import com.epn.casino.model.user.User;

/**
 * Contrato de comportamiento que independiza el cálculo monetario.
 * Reemplaza a la interfaz 'Apostable' del diagrama original.
 */
public interface Playable
{
    boolean processBet(User user, double amount);
    double calculatePayout(GameResult result, double betAmount);
}