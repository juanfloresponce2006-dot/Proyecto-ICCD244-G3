package base.mainProgram.Games.Roulette;

import base.accountsHandler.User;
import base.mainProgram.Games.Game;
import base.mainProgram.Games.GameResult;
import base.mainProgram.Games.Playable;

import javax.swing.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class RouletteConsole extends Game implements Playable {

    private final int[] rojos = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
    private final int[] negros = {2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35};

    private final String userName;
    private double userBalance;

    private int typeHandler;
    private final int[] rangeHandler = new int[37];

    public RouletteConsole(User user) {
        String gameNameSlots = "Roulette";
        double minimumBetSlots = 1;

        super(gameNameSlots, minimumBetSlots);
        this.userName = user.getName();
        this.userBalance = user.getBalance();
    }

    public double mainGameFlow (){

        Scanner tempKey = new Scanner(System.in);

        double userBet,fixUserBalance = userBalance;
        boolean canPlay = false;

        while (hasEnoughMoney(userBalance)){
            canPlay = true;
            System.out.println("Buena suerte, "+userName);
            System.out.println("Balance: $"+ userBalance);

            System.out.println("Cuánto apuestas? mínimo $"+minimumBet);
            while (true){
                try {
                    userBet = tempKey.nextDouble();
                    if (userBet < minimumBet) System.out.println("estás mal de la cabeza? minimo $"+minimumBet);
                    else break;
                }catch (InputMismatchException _){
                    System.out.println("(usa comas we):");
                }
            }

            tempKey.nextLine();


            System.out.println("A qué le apuestas? mira un tutorial si no sabes lol");

            System.out.println("Ingresa el tipo (1-5) 6 para especiales");
            typeHandler = tempKey.nextInt();
            tempKey.nextLine();

            System.out.println("Ingresa los cuadrados (1, 2, 3, 4, 6 según el tipo, -1 para rojos" +
                    "-2 para negros, -3 al -5 para las docenas, -6 o -7 para bajos y altos, -8 pares y -9 impares");

            for (int i = 0; i < 6; i++) {
                System.out.println("cuadrado " + (i + 1) + ": ");
                rangeHandler[i] = tempKey.nextInt();
                tempKey.nextLine();
                //especiales
                if (rangeHandler[0] < 0) {

                    switch (rangeHandler[0]) {
                        case -1:
                            System.arraycopy(rojos, 0, rangeHandler, 0, rojos.length - 1);
                            typeHandler = 7;
                            break;
                        case -2:
                            System.arraycopy(negros, 0, rangeHandler, 0, negros.length - 1);
                            typeHandler = 7;
                            break;
                        case -3:
                            for (int j = 0; j < 12; j++) {
                                rangeHandler[j] = j + 1;
                            }
                            typeHandler = 6;
                            break;
                        case -4:
                            for (int j = 0; j < 12; j++) {
                                rangeHandler[j] = j + 13;
                            }
                            typeHandler = 6;
                            break;
                        case -5:
                            for (int j = 0; j < 12; j++) {
                                rangeHandler[j] = j + 25;
                            }
                            typeHandler = 6;
                            break;
                        case -6:
                            for (int j = 0; j < 18; j++) {
                                rangeHandler[j] = j + 1;
                            }
                            typeHandler = 7;
                            break;
                        case -7:
                            for (int j = 0; j < 18; j++) {
                                rangeHandler[j] = j + 19;
                            }
                            typeHandler = 7;
                            break;
                        case -8:
                            for (int j = 0; j < 18; j++) {
                                rangeHandler[j] = (j * 2) + 2;
                            }
                            typeHandler = 7;
                            break;
                        case -9:
                            for (int j = 0; j < 18; j++) {
                                rangeHandler[j] = (j * 2) + 1;
                            }
                            typeHandler = 7;
                            break;
                    }break;

                }
                //pleno
                if (typeHandler == 1) break;
                    //split
                else if (typeHandler == 2) {
                    if (i >= 1) break;
                }
                //street
                else if (typeHandler == 3) {
                    if (i >= 2) break;
                }
                //square
                else if (typeHandler == 4) {
                    if (i >= 3) break;
                }
                //línea por defecto rango max
            }

            userBalance += resultInBalance(play(new JFrame()),userBet);

            System.out.println("balance actual: "+userBalance);
            System.out.println("Volver a jugar? 1. Sí 2. No");
            int pick;
            pick = tempKey.nextInt();
            tempKey.nextLine();
            if (pick == 2) break;
        }
        if (!canPlay) System.out.println("no podés jugar, pobre");
        return userBalance - fixUserBalance;
    }

    @Override
    public GameResult play(JFrame frame) {

        int num = (int)Math.round(Math.random()*37);
        System.out.println("salió el número "+num);

        switch (typeHandler){
            case 1:
                if (rangeHandler[0] == num) return new GameResult(true,35);
                break;
            case 2:
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,17);
                }break;
            case 3:
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,11);
                }break;
            case 4  :
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,8);
                }break;
            case 5:
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,5);
                }break;
            case 6:
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,2);
                }break;
            case 7:
                for (int i : rangeHandler){
                    if(i == num) return new GameResult(true,1);
                }break;
        }

        return new GameResult(false,-1);
    }

    @Override
    public double resultInBalance(GameResult result, double betAmount) {

        return betAmount*result.getMultiplier();

    }
}
