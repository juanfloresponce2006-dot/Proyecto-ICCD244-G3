package base.mainProgram;

import base.accountsHandler.Data;
import base.accountsHandler.User;

import java.util.Scanner;

public class ModifyUserDataConsole {

    public static void execChange(User userToChange) {
        System.out.println(userToChange.toString());

        Scanner keyboard = new Scanner(System.in);

        int choice, exit = 0;

        while (exit == 0){
            System.out.println("choice: 1. cambiar nombre 2. cambiar email 3. cambiar contraseña 4. volver");
            choice = keyboard.nextInt();
            keyboard.nextLine();

            switch (choice){
                case 1:
                    System.out.println("Ingrese el nuevo nombre");
                    String name = keyboard.nextLine();
                    Data.editName(userToChange,name);
                    break;
                case 2:
                    System.out.println("Ingrese el nuevo email");
                    String email = keyboard.nextLine();
                    Data.editEmail(userToChange,email);
                    break;
                case 3:
                    System.out.println("Ingrese la nueva contraseña");
                    String password = keyboard.nextLine();
                    Data.editPassword(userToChange,password);
                    break;
                case 4:
                    exit = 1;
            }
        }
    }

}
