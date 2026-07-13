package com.epn.casino.model.financial;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una transacción financiera inmutable.
 * Utiliza 'record' (JDK 14+) para garantizar que los datos no puedan ser alterados una vez creados.
 */
public record Transaction(
        String transactionId,
        double amount,
        TransactionType type,
        LocalDateTime timestamp
) implements Serializable {
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, BET_PLACED, WINNING_PAYOUT
    }

    // Constructor compacto para autogenerar el ID y el Timestamp
    public Transaction(double amount, TransactionType type) {
        this(UUID.randomUUID().toString(), amount, type, LocalDateTime.now());
    }
}