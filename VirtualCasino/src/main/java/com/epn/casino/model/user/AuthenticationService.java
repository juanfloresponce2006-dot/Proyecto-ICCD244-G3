package com.epn.casino.model.user;

import com.epn.casino.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

public class AuthenticationService {

    private final Map<String, User> registeredUsers;
    private User currentUser;
    private final UserRepository userRepository; // Inyección de dependencia

    public AuthenticationService() {
        this.userRepository = new UserRepository();
        // Cargamos los datos desde el disco duro al iniciar el servicio
        this.registeredUsers = userRepository.loadUsers();
        this.currentUser = null;
    }

    public boolean register(String username, String email, String password, double initialDeposit) {
        if (registeredUsers.containsKey(email.toLowerCase())) {
            return false;
        }
        User newUser = new User(username, email.toLowerCase(), password, initialDeposit);
        registeredUsers.put(email.toLowerCase(), newUser);
        return true;
    }

    public boolean login(String email, String password) {
        User user = registeredUsers.get(email.toLowerCase());
        if (user != null && user.getPasswordHash().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Nuevo método: Obliga al repositorio a guardar el estado actual en el disco duro.
     */
    public void saveState() {
        userRepository.saveUsers(registeredUsers);
    }
}