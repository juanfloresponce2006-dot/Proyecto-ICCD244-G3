package com.epn.casino.controller;

import com.epn.casino.MainApp;
import com.epn.casino.model.game.GameResult;
import com.epn.casino.model.game.slot.SlotMachine;
import com.epn.casino.model.financial.Transaction;
import com.epn.casino.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controlador de la interfaz gráfica del Tragamonedas.
 * Conecta las interacciones del usuario con la lógica del juego y la billetera.
 */
public class SlotController {

    @FXML private Label lblBalance;
    @FXML private Label lblResult;

    private User currentUser;
    private SlotMachine slotMachine;
    private final double BET_AMOUNT = 10.0; // Apuesta fija por giro para esta iteración

    public void initData(User user) {
        this.currentUser = user;
        // Instanciamos el motor lógico de 3 rodillos que ya validamos en el backend
        this.slotMachine = new SlotMachine("SLOT-01", "Neon Slots", BET_AMOUNT, 3);
        updateBalanceDisplay();
    }

    @FXML
    private void handleSpin(ActionEvent event) {
        // 1. Delegamos el procesamiento financiero a la clase Game (Polimorfismo)
        if (slotMachine.processBet(currentUser, BET_AMOUNT)) {

            // 2. Ejecutamos el algoritmo de azar
            GameResult result = slotMachine.play();

            // 3. Extraemos la descripción inmutable del record y la mostramos en pantalla
            lblResult.setText(result.description());

            // 4. Calculamos y procesamos el premio si es ganador
            double payout = slotMachine.calculatePayout(result, BET_AMOUNT);
            if (payout > 0) {
                currentUser.getWallet().creditAmount(payout, Transaction.TransactionType.WINNING_PAYOUT);
            }

            updateBalanceDisplay();

        } else {
            lblResult.setText("Insufficient funds for bet ($" + BET_AMOUNT + ")");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        // Enrutamos de vuelta al Dashboard manteniendo la sesión activa
        javafx.fxml.FXMLLoader loader = MainApp.getLoaderAndSwitch("/com/epn/casino/views/dashboard.fxml");
        DashboardController dashboardController = loader.getController();
        dashboardController.initData(currentUser);
    }

    private void updateBalanceDisplay() {
        lblBalance.setText(String.format("Balance: $%.2f", currentUser.getWallet().getCurrentBalance()));
    }
}