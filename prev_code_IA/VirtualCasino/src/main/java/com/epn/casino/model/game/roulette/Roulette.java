package com.epn.casino.model.game.roulette;

import com.epn.casino.model.game.Game;
import com.epn.casino.model.game.GameResult;

import java.util.Random;

/**
 * Especialización concreta del juego de Ruleta.
 * Implementa la lógica de giro y resolución de apuestas de tipo 'Pleno' (Número exacto).
 */
public class Roulette extends Game {

    private final String rouletteType;
    private final Random randomGenerator;
    private int currentTargetNumber; // El número al que el usuario decide apostar

    public Roulette(String gameId, String gameName, double minimumBet, String rouletteType) {
        super(gameId, gameName, minimumBet);
        this.rouletteType = rouletteType; // Ejemplo: "EUROPEAN" (0-36)
        this.randomGenerator = new Random();
        this.currentTargetNumber = -1; // Estado inicial sin apuesta definida
    }

    /**
     * Define el número al que el jugador está apostando en esta ronda.
     * En un entorno real, esto provendría del controlador de la interfaz gráfica.
     */
    public void setTargetNumber(int targetNumber) {
        if (targetNumber >= 0 && targetNumber <= 36) {
            this.currentTargetNumber = targetNumber;
        } else {
            throw new IllegalArgumentException("Invalid roulette number. Must be between 0 and 36.");
        }
    }

    /**
     * Ejecuta el giro de la ruleta y evalúa la victoria.
     * Cumple con la firma obligatoria de la clase abstracta Game.
     */
    @Override
    public GameResult play() {
        if (currentTargetNumber == -1) {
            return new GameResult(false, 0.0, "Error: No target number selected before spinning.");
        }

        int winningNumber = spinRoulette();
        String winningColor = determineColor(winningNumber);

        // Lógica de victoria: El número girado coincide con la apuesta del jugador
        boolean isWinner = (winningNumber == currentTargetNumber);

        // El pago estándar de ruleta por acertar un pleno es 36 veces la apuesta
        double appliedMultiplier = isWinner ? 36.0 : 0.0;

        // Estructuramos el resultado inyectando el número y el color
        String description = String.format("Spun: %d (%s) | Your bet: %d - %s",
                winningNumber, winningColor, currentTargetNumber, isWinner ? "WINNER!" : "House wins.");

        // Limpiamos el objetivo para obligar al usuario a apostar nuevamente en el siguiente turno
        currentTargetNumber = -1;

        return new GameResult(isWinner, appliedMultiplier, description);
    }

    @Override
    public double calculatePayout(GameResult result, double betAmount) {
        if (result.isWinner()) {
            return betAmount * result.multiplier();
        }
        return 0.0;
    }

    /**
     * Simula la física de la ruleta generando un número aleatorio del 0 al 36.
     * Equivalente al método 'girarRuleta()' del UML.
     */
    private int spinRoulette() {
        // nextInt(37) genera un número inclusivo entre 0 y 36
        return randomGenerator.nextInt(37);
    }

    /**
     * Calcula el color de la casilla ganadora basándose en la disposición estándar del paño de ruleta.
     */
    private String determineColor(int number) {
        if (number == 0) {
            return "GREEN";
        }

        // Lógica estándar de colores en la ruleta:
        // Del 1 al 10 y del 19 al 28: impares son rojos, pares son negros.
        // Del 11 al 18 y del 29 al 36: impares son negros, pares son rojos.
        if ((number >= 1 && number <= 10) || (number >= 19 && number <= 28)) {
            return (number % 2 != 0) ? "RED" : "BLACK";
        } else {
            return (number % 2 != 0) ? "BLACK" : "RED";
        }
    }
}