package com.epn.casino.controller;

import com.epn.casino.repository.UserRepository;
import java.util.Map;
import com.epn.casino.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador principal del Dashboard.
 * Intermediario entre la interfaz gráfica y los datos del usuario.
 */
public class DashboardController {

    // La anotación @FXML enlaza estas variables con los elementos visuales
    @FXML private Label lblUsername;
    @FXML private Label lblBalance;

    private User currentUser;

    /**
     * Inyección de dependencia manual.
     * Este método será llamado inmediatamente después de cargar la pantalla
     * para transferir el usuario autenticado desde el Login al Dashboard.
     */
    public void initData(User user) {
        this.currentUser = user;
        lblUsername.setText("Player: " + user.getUsername().toUpperCase());
        updateBalanceDisplay();
    }

    /**
     * Refresca el texto en pantalla leyendo el dato real de la billetera.
     */
    public void updateBalanceDisplay() {
        if (currentUser != null) {
            // Formateo a 2 decimales para estética financiera
            lblBalance.setText(String.format("Balance: $%.2f", currentUser.getWallet().getCurrentBalance()));
        }
    }

    @FXML
    private void launchSlots(ActionEvent event) {
        System.out.println("[UI] Loading Neon Slots...");
        try {
            javafx.fxml.FXMLLoader loader = com.epn.casino.MainApp.getLoaderAndSwitch("/com/epn/casino/views/slots.fxml");
            SlotController controller = loader.getController();
            controller.initData(currentUser); // Pasamos la sesión al juego
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void launchRoulette(ActionEvent event) {
        System.out.println("[UI] Loading European Roulette...");
        try {
            javafx.fxml.FXMLLoader loader = com.epn.casino.MainApp.getLoaderAndSwitch("/com/epn/casino/views/roulette.fxml");
            RouletteController controller = loader.getController();
            controller.initData(currentUser); // Pasamos la sesión al juego
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        System.out.println("[UI] Initiating secure logout protocol...");

        try {
            if (currentUser != null) {
                // 1. Instanciamos el repositorio para acceder a la base de datos binaria
                UserRepository repo = new UserRepository();

                // 2. Cargamos el estado actual de todos los usuarios desde el disco
                Map<String, User> usersDatabase = repo.loadUsers();

                // 3. Sobrescribimos el registro específico de nuestro jugador activo
                // con su objeto actual (el cual contiene la billetera y el saldo actualizado)
                usersDatabase.put(currentUser.getEmail(), currentUser);

                // 4. Ejecutamos la serialización para guardar los cambios físicamente
                repo.saveUsers(usersDatabase);
                System.out.println("[UI] Session saved successfully. Final balance: $"
                        + currentUser.getWallet().getCurrentBalance());
            }

            // 5. Destruimos la sesión en memoria por seguridad
            this.currentUser = null;

            // 6. Enrutamos de regreso a la pantalla de Login
            com.epn.casino.MainApp.switchScene("/com/epn/casino/views/login.fxml");

        } catch (java.io.IOException e) {
            System.err.println("[UI] Critical error loading Login screen: " + e.getMessage());
        }
    }
}