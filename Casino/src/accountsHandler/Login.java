package accountsHandler;

import java.util.Scanner;

public class Login {

    public static User execLogin(){

        Scanner keyboard = new Scanner(System.in);

        String email, password;

        System.out.println("Ingrese su email");
        email = keyboard.nextLine();
        System.out.println("Ingrese su contraseña");
        password = keyboard.nextLine();

        if(Data.retrieveUserByEmail(email) == null){
            System.out.println("No such user");
        }else {
            User checkUser = Data.retrieveUserByEmail(email);
            if(checkUser.getPassword().equals(password)){
                System.out.println("ingreso exitoso");
                return checkUser;
            }else {
                System.out.println("contraseña incorrecta");
            }
        }

        return null;
    }
    
}
