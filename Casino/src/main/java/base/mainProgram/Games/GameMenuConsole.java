package base.mainProgram.Games;

import base.accountsHandler.User;
import base.mainProgram.Games.Roulette.RouletteConsole;

import java.util.Scanner;

public class GameMenuConsole {

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
                    RouletteConsole newRouletteGame = new RouletteConsole(loggedUser);

                    loggedUser.setBalance(loggedUser.getBalance()+newRouletteGame.mainGameFlow());
                    break;
                case 2:
                    System.out.println("slots");
                    //SlotsConsole newSlotsGame = new SlotsConsole(loggedUser);

                    //loggedUser.setBalance(loggedUser.getBalance()+newSlotsGame.mainGameFlow());
                    break;
                case 3:
                    exit = 1;
            }
        }
    }

}
