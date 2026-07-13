package base.accountsHandler;

import java.io.*;
import java.util.ArrayList;

public class Data {

    private static final String FILE_PATH = "casino_users.dat";
    private static ArrayList<User> users = new ArrayList<>();


    static {
        loadData();
    }

    // Carga los usuarios desde el archivo al ArrayList
    @SuppressWarnings("unchecked")
    private static void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (ArrayList<User>) ois.readObject();
                User.numberOfUsers = users.size();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar la base de datos de usuarios: " + e.getMessage());
            }
        }
    }

    // Guarda el estado actual del ArrayList
    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error al guardar en la base de datos: " + e.getMessage());
        }
    }

    public static void storeUser(User user) throws DuplicateUserException, IncompleteUserDataException {
        for (User iUser : users){
            if(iUser.getEmail().equals(user.getEmail())){
                throw new DuplicateUserException("User already exists");
            }
        }
        if(user.getEmail().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
        if(user.getPassword().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
        if(user.getName().isBlank()) user.setName(user.getEmail());

        users.add(user);
        saveData();
    }

    public static void editUser(User user) throws IncompleteUserDataException {
        for (User iUser : users){
            if(iUser.getEmail().equals(user.getEmail())){

                if(user.getEmail().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
                if(user.getPassword().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
                if(user.getName().isBlank()) user.setName(user.getEmail());

                users.remove(iUser);
                users.add(user);
                saveData();
                return;
            }
        }

        String prevEmail = user.getEmail();
        User removeUser = retrieveUserByEmail(prevEmail);

        if(user.getEmail().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
        if(user.getPassword().isBlank()) throw new IncompleteUserDataException("Incomplete data to store user");
        if(user.getName().isBlank()) user.setName(user.getEmail());

        users.remove(removeUser);
        users.add(user);
        saveData();
    }

    public static User retrieveUserByEmail(String email){
        User returnedUser = null;
        for (User user : users){
            if(email.equals(user.getEmail())){
                returnedUser = user;
            }
        }
        return returnedUser;
    }


    public static void editName(User user, String name){
        user.setName(name);
    }
    public static void editEmail(User user, String email){
        user.setEmail(email);
    }
    public static void editPassword(User user, String password){
        user.setPassword(password);
    }
    public static void editBalance(User user, double balance){
        user.setBalance(user.getBalance() + balance);
    }

    public static ArrayList<User> retrieveAllUsers(){
        return users;
    }
}