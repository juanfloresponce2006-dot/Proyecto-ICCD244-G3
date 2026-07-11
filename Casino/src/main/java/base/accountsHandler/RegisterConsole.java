package base.accountsHandler;

import java.util.Scanner;

public class RegisterConsole {

    public static void execRegister(){

        Scanner keyboard = new Scanner(System.in);

        String name = null, email = null, password = null;

        while (name == null || email == null || password == null){

            System.out.println("Ingrese el nombre");
            name = keyboard.nextLine();
            System.out.println("Ingrese el email");
            email = keyboard.nextLine();
            System.out.println("Ingrese el password");
            password = keyboard.nextLine();

            try{
                Data.storeUser(new User(name,email,password,0));
            }catch (DuplicateUserException e){
                email = null;
            }catch (Exception e){
                //unhandled
            }
        }
    }

}
