package mainProgram;

import accountsHandler.User;
import mainProgram.Games.GameMenu;

import java.util.Scanner;

public class Home {

    public static void execHome(User loggedUser){
        System.out.println("Bienvenido "+loggedUser.getName());

        Scanner keyboard = new Scanner(System.in);

        int choice, exit = 0;

        while (exit == 0){
            System.out.println("choice: 1. jugar 2. recargar 3. cambiar datos 4. volver");
            choice = keyboard.nextInt();
            keyboard.nextLine();

            switch (choice){
                case 1:
                    GameMenu.execGames(loggedUser);
                    break;
                case 2:
                    Payment.execPayment(loggedUser);
                    break;
                case 3:
                    ModifyUserData.execChange(loggedUser);

                    break;
                case 4:
                    exit = 1;
            }
        }
    }

}
