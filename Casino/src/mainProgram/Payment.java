package mainProgram;

import accountsHandler.Data;
import accountsHandler.User;

import java.util.Scanner;

public class Payment {

    public static void execPayment(User user){

        Scanner keyboard = new Scanner(System.in);

        double recarga;
        System.out.println("Págame we");
        recarga = keyboard.nextDouble();
        keyboard.nextLine();

        Data.editBalance(user,recarga);
    }

}
