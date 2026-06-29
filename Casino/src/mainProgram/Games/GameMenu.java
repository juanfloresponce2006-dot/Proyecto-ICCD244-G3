package mainProgram.Games;

import accountsHandler.User;
import mainProgram.Games.Roulette.Roulette;
import mainProgram.Games.Slots.Slots;

import java.util.Scanner;

public class GameMenu {

    public static void execGames (User loggedUser){

        Scanner keyboard = new Scanner(System.in);

        int choice, exit = 0;

        while (exit == 0){
            System.out.println("choice: 1. ruleta 2. slots 3. volver");
            choice = keyboard.nextInt();
            keyboard.nextLine();

            switch (choice){
                case 1:
                    System.out.println("ruleta");
                    Roulette newRouletteGame = new Roulette(loggedUser);

                    loggedUser.setBalance(loggedUser.getBalance()+newRouletteGame.mainGameFlow());
                    break;
                case 2:
                    System.out.println("slots");
                    Slots newSlotsGame = new Slots(loggedUser);

                    loggedUser.setBalance(loggedUser.getBalance()+newSlotsGame.mainGameFlow());
                    break;
                case 3:
                    exit = 1;
            }
        }
    }

}
