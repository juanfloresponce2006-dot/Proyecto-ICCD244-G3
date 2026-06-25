package com.epn.casino;

/**
 * Clase de arranque proxy.
 * Su única responsabilidad es eludir la restricción estricta de módulos de JavaFX
 * llamando al método main de la clase Application de forma indirecta.
 */
public class AppLauncher {
    public static void main(String[] args) {
        // Llamamos al método main estático de nuestro motor principal
        MainApp.main(args);
    }
}