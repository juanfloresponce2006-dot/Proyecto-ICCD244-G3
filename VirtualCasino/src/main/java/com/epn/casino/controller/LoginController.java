package com.epn.casino.controller;

import com.epn.casino.model.user.AuthenticationService;
import com.epn.casino.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controlador de la vista de inicio de sesión.
 * Gestiona la captura de credenciales y la comunicación con el servicio de autenticación.
 */
public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private AuthenticationService authService;

    /**
     * El framework JavaFX llama automáticamente a este método
     * justo después de inyectar los elementos @FXML.
     */
    @FXML
    public void initialize() {
        // Instanciamos el servicio que automáticamente carga los datos del disco duro
        this.authService = new AuthenticationService();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        lblError.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both email and password.");
            return;
        }

        if (authService.login(email, password)) {
            User loggedInUser = authService.getCurrentUser().get();
            System.out.println("[UI] Login successful. Authenticated as: " + loggedInUser.getUsername());

            try {
                // Usamos nuestro nuevo enrutador de MainApp para cargar el Dashboard
                javafx.fxml.FXMLLoader loader = com.epn.casino.MainApp.getLoaderAndSwitch("/com/epn/casino/views/dashboard.fxml");

                // Obtenemos el controlador del Dashboard que acaba de crearse...
                DashboardController dashboardController = loader.getController();

                // ... y le inyectamos el usuario autenticado
                dashboardController.initData(loggedInUser);

            } catch (java.io.IOException e) {
                lblError.setText("Error loading Dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            lblError.setText("Invalid credentials. Try again or register.");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            com.epn.casino.MainApp.switchScene("/com/epn/casino/views/register.fxml");
        } catch (java.io.IOException e) {
            lblError.setText("Error loading Registration screen.");
            e.printStackTrace();
        }
    }
}