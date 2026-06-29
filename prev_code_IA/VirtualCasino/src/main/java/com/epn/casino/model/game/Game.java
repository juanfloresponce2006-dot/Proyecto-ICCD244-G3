package com.epn.casino.model.game;

import com.epn.casino.model.user.User;
import com.epn.casino.model.financial.Transaction;

/**
 * Clase base para todos los juegos del casino.
 * Centraliza atributos comunes y delega la ejecución específica a las subclases.
 */
public abstract class Game implements Playable {
    protected final String gameId;
    protected final String gameName;
    protected final double minimumBet;
    protected GameState state;

    public enum GameState { ACTIVE, MAINTENANCE }

    public Game(String gameId, String gameName, double minimumBet) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.minimumBet = minimumBet;
        this.state = GameState.ACTIVE;
    }

    public String getGameName() { return gameName; }

    @Override
    public boolean processBet(User user, double amount) {
        if (state != GameState.ACTIVE) {
            System.out.println("Game is currently under maintenance.");
            return false;
        }
        if (amount < minimumBet) {
            System.out.println("Bet amount is lower than the minimum required: $" + minimumBet);
            return false;
        }

        // El cobro de la apuesta se procesa de forma atómica a través de la billetera
        return user.getWallet().debitAmount(amount, Transaction.TransactionType.BET_PLACED);
    }

    /**
     * Método polimórfico principal.
     * Cada juego concreto (Ruleta, Slots) definirá su propio algoritmo de azar aquí.
     */
    public abstract GameResult play();
}