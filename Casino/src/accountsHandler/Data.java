package accountsHandler;

import java.util.ArrayList;

public class Data {

    private static ArrayList<User> users = new ArrayList<>();

    public static void storeUser (User user) throws DuplicateUserException {
        for (User iUser : users){
            if(iUser.getEmail().equals(user.getEmail())){
                throw new DuplicateUserException("accountsHandler.User already exists");
            }
        }
        users.add(user);
    }

    public static User retrieveUserByEmail (String email){
        User returnedUser = null;
        for (User user : users){
            if(email.equals(user.getEmail())){
                returnedUser = user;
            }
        }

        return returnedUser;
    }

    public static void editName (User user, String name){
        user.setName(name);
    }
    public static void editEmail (User user, String email){
        user.setEmail(email);
    }
    public static void editPassword (User user, String password){
        user.setPassword(password);
    }
    public static void editBalance (User user, double balance){
        user.setBalance(user.getBalance()+balance);
    }

    /*int getUserIndex (User user){
        for(User user1 : users){
            if(user1.getEmail().equals(user.getEmail())){
                return users.indexOf(user1);
            }
        }
        return -1;
    }*/

    public static ArrayList<User> retrieveAllUsers (){
        return users;
    }
}