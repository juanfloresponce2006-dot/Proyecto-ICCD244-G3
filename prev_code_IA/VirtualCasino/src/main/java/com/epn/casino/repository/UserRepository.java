package com.epn.casino.repository;

import com.epn.casino.model.user.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Repositorio encargado de la persistencia de datos de los usuarios.
 * Utiliza serialización binaria para almacenar perfiles y saldos en disco.
 */
public class UserRepository {

    // Archivo físico donde se guardarán los datos de los usuarios
    private static final String FILE_PATH = "casino_users.dat";

    /**
     * Guarda el mapa de usuarios en el sistema de archivos de forma binaria.
     */
    public void saveUsers(Map<String, User> users) {
        // Try-With-Resources cierra automáticamente el flujo de datos al terminar
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
            System.out.println("[Repository] User data successfully saved to disk.");
        } catch (IOException e) {
            System.err.println("[Repository] Critical error saving users: " + e.getMessage());
        }
    }

    /**
     * Carga el mapa de usuarios desde el sistema de archivos.
     * Si el archivo no existe (primera ejecución), devuelve un nuevo HashMap.
     */
    @SuppressWarnings("unchecked")
    public Map<String, User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("[Repository] No existing data file found. Starting with a clean database.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                System.out.println("[Repository] User data successfully loaded.");
                return (Map<String, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Repository] Error reading user file: " + e.getMessage());
        }

        // Fallback de seguridad en caso de que el archivo esté corrupto
        return new HashMap<>();
    }
}