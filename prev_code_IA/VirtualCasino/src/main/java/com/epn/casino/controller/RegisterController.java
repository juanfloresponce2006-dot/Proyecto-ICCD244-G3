package com.epn.casino.controller;

import com.epn.casino.MainApp;
import com.epn.casino.model.user.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controlador de la vista de Registro.
 * Gestiona la creación de nuevos usuarios y su persistencia inmediata.
 */
public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtDeposit;
    @FXML private Label lblError;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        // Carga la base de datos de usuarios actuales
        this.authService = new AuthenticationService();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String depositStr = txtDeposit.getText().trim();

        // Validaciones básicas de la UI
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || depositStr.isEmpty()) {
            lblError.setText("All fields are required.");
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(depositStr);
            if (initialDeposit < 10) {
                lblError.setText("Minimum initial deposit is $10.");
                return;
            }

            // Delegamos al backend el registro
            if (authService.register(username, email, password, initialDeposit)) {
                // Si es exitoso, forzamos el guardado en el archivo .dat inmediatamente
                authService.saveState();
                System.out.println("[UI] New user registered successfully: " + email);

                // Regresamos automáticamente a la pantalla de Login
                goBack(event);
            } else {
                lblError.setText("Error: Email is already registered.");
            }
        } catch (NumberFormatException e) {
            lblError.setText("Error: Deposit must be a valid number.");
        } catch (IOException e) {
            lblError.setText("System error navigating to login.");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        MainApp.switchScene("/com/epn/casino/views/login.fxml");
    }
}