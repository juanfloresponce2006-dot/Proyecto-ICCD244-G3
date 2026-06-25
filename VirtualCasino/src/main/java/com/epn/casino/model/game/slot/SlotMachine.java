package com.epn.casino.model.game.slot;

import com.epn.casino.model.game.Game;
import com.epn.casino.model.game.GameResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Especialización concreta del juego de Tragamonedas.
 * Implementa el algoritmo de generación de combinaciones y resolución de premios.
 */
public class SlotMachine extends Game {

    private final int numberOfReels;
    private final Random randomGenerator;

    /**
     * Enumerador interno que define los símbolos permitidos y su valor multiplicador base.
     * Reemplaza la estructura 'Simbolo' del diagrama original.
     */
    public enum Symbol {
        CHERRY(2.0),
        BAR(5.0),
        SEVEN(10.0),
        DIAMOND(50.0);

        private final double multiplier;

        Symbol(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    public SlotMachine(String gameId, String gameName, double minimumBet, int numberOfReels) {
        super(gameId, gameName, minimumBet);
        this.numberOfReels = numberOfReels;
        // Instanciamos el generador de números aleatorios una sola vez para eficiencia
        this.randomGenerator = new Random();
    }

    /**
     * Ejecuta el giro de los rodillos y evalúa la victoria.
     * Aplica el overriding exigido por la clase padre Game.
     */
    @Override
    public GameResult play() {
        List<Symbol> combination = generateCombination();
        boolean isWinner = checkWinCondition(combination);

        // Si gana, toma el multiplicador del símbolo ganador; si no, el multiplicador es 0.
        double appliedMultiplier = isWinner ? combination.get(0).getMultiplier() : 0.0;

        // Formateamos una descripción para el historial o la futura interfaz gráfica
        String description = "Roll: " + combination.toString() + (isWinner ? " - JACKPOT!" : " - No win.");

        return new GameResult(isWinner, appliedMultiplier, description);
    }

    @Override
    public double calculatePayout(GameResult result, double betAmount) {
        if (result.isWinner()) {
            return betAmount * result.multiplier();
        }
        return 0.0; // El usuario pierde su apuesta
    }

    /**
     * Simula el giro independiente de los rodillos (reels).
     */
    private List<Symbol> generateCombination() {
        List<Symbol> reels = new ArrayList<>();
        Symbol[] allSymbols = Symbol.values();

        for (int i = 0; i < numberOfReels; i++) {
            // Selecciona un símbolo al azar basado en la longitud del enumerador
            int randomIndex = randomGenerator.nextInt(allSymbols.length);
            reels.add(allSymbols[randomIndex]);
        }
        return reels;
    }

    /**
     * Lógica de negocio: El jugador gana solo si todos los rodillos tienen el mismo símbolo.
     */
    private boolean checkWinCondition(List<Symbol> combination) {
        if (combination == null || combination.isEmpty()) {
            return false;
        }

        Symbol firstSymbol = combination.get(0);
        for (Symbol symbol : combination) {
            if (symbol != firstSymbol) {
                return false; // Al primer símbolo distinto, se rompe la línea ganadora
            }
        }
        return true;
    }
}