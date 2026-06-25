package com.epn.casino.controller;

import com.epn.casino.MainApp;
import com.epn.casino.model.game.GameResult;
import com.epn.casino.model.game.roulette.Roulette;
import com.epn.casino.model.financial.Transaction;
import com.epn.casino.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controlador de la interfaz gráfica de la Ruleta.
 * Gestiona la entrada de datos del usuario y la vincula al motor de azar.
 */
public class RouletteController {

    @FXML private Label lblBalance;
    @FXML private Label lblResult;
    @FXML private TextField txtTargetNumber;

    private User currentUser;
    private Roulette roulette;
    private final double BET_AMOUNT = 20.0; // Apuesta fija por giro

    public void initData(User user) {
        this.currentUser = user;
        // Instanciamos el motor lógico de Ruleta Europea que validamos previamente
        this.roulette = new Roulette("ROUL-01", "European Roulette", 10.0, "EUROPEAN");
        updateBalanceDisplay();
    }

    @FXML
    private void handleSpin(ActionEvent event) {
        // 1. Captura y validación del Input del usuario
        String input = txtTargetNumber.getText().trim();
        int target;

        try {
            target = Integer.parseInt(input);
            if (target < 0 || target > 36) {
                lblResult.setText("Invalid bet. Choose a number between 0 and 36.");
                return;
            }
        } catch (NumberFormatException e) {
            lblResult.setText("Error: Please enter a numeric value.");
            return;
        }

        // 2. Procesamiento de la apuesta (Polimorfismo)
        if (roulette.processBet(currentUser, BET_AMOUNT)) {

            // 3. Inyección del estado y ejecución del juego
            roulette.setTargetNumber(target);
            GameResult result = roulette.play();

            // 4. Renderizado y pago
            lblResult.setText(result.description());
            double payout = roulette.calculatePayout(result, BET_AMOUNT);

            if (payout > 0) {
                currentUser.getWallet().creditAmount(payout, Transaction.TransactionType.WINNING_PAYOUT);
            }

            updateBalanceDisplay();
            txtTargetNumber.clear(); // Limpiamos el campo para la siguiente jugada

        } else {
            lblResult.setText("Insufficient funds for bet ($" + BET_AMOUNT + ")");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        javafx.fxml.FXMLLoader loader = MainApp.getLoaderAndSwitch("/com/epn/casino/views/dashboard.fxml");
        DashboardController dashboardController = loader.getController();
        dashboardController.initData(currentUser);
    }

    private void updateBalanceDisplay() {
        lblBalance.setText(String.format("Balance: $%.2f", currentUser.getWallet().getCurrentBalance()));
    }
}