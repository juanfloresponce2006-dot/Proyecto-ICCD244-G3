package com.epn.casino.model.user;

import java.io.Serializable;
import com.epn.casino.model.financial.Wallet;
import java.util.UUID;

/**
 * Entidad de dominio que representa a un jugador en el casino.
 */
public class User implements Serializable {
    private final String userId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Wallet wallet; // Composición estricta

    public User(String username, String email, String passwordHash, double initialDeposit) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        // El usuario nace con un estado de cuenta fuertemente ligado a su ciclo de vida
        this.wallet = new Wallet(initialDeposit);
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    /**
     * Reemplaza a consultarEstadoCuenta() del UML original.
     * Expone la billetera para operaciones seguras.
     */
    public Wallet getWallet() { return wallet; }
}