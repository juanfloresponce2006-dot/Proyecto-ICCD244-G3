package com.epn.casino.model.financial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gestiona el saldo del usuario y el historial de transacciones.
 * Implementa Thread-Safety para cumplir con el RNF-01 (Seguridad Financiera).
 */
public class Wallet implements Serializable {
    private double currentBalance;
    private final List<Transaction> transactionHistory;
    private final ReentrantLock lock; // Garantiza operaciones atómicas

    public Wallet(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.currentBalance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.lock = new ReentrantLock();

        if (initialBalance > 0) {
            recordTransaction(initialBalance, Transaction.TransactionType.DEPOSIT);
        }
    }

    /**
     * Reemplaza el método acreditar(monto) del diagrama UML.
     */
    public void creditAmount(double amount, Transaction.TransactionType type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive.");
        }
        lock.lock();
        try {
            currentBalance += amount;
            recordTransaction(amount, type);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reemplaza el método debitar(monto) del diagrama UML.
     */
    public boolean debitAmount(double amount, Transaction.TransactionType type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive.");
        }
        lock.lock();
        try {
            if (currentBalance >= amount) {
                currentBalance -= amount;
                recordTransaction(amount, type);
                return true;
            }
            return false; // Saldo insuficiente
        } finally {
            lock.unlock();
        }
    }

    public double getCurrentBalance() {
        lock.lock();
        try {
            return currentBalance;
        } finally {
            lock.unlock();
        }
    }

    public List<Transaction> getTransactionHistory() {
        lock.lock();
        try {
            // Devuelve una lista de solo lectura para evitar mutaciones externas
            return Collections.unmodifiableList(new ArrayList<>(transactionHistory));
        } finally {
            lock.unlock();
        }
    }

    private void recordTransaction(double amount, Transaction.TransactionType type) {
        transactionHistory.add(new Transaction(amount, type));
    }
}