package mainProgram.Games.Slots;

import accountsHandler.User;
import mainProgram.Games.Game;
import mainProgram.Games.GameResult;
import mainProgram.Games.Playable;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Slots extends Game implements Playable {

    private final int[] numRoll1 = {1,2,1,3,1,2,1,1,4,2,1,3,1,2,1,1,3,2,1,2};
    private final int[] numRoll2 = {2,1,1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1};
    private final int[] numRoll3 = {1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1,2,1};

    private final SimpleSlotRoll roll1 = new SimpleSlotRoll(numRoll1);
    private final SimpleSlotRoll roll2 = new SimpleSlotRoll(numRoll2);
    private final SimpleSlotRoll roll3 = new SimpleSlotRoll(numRoll3);

    private final String userName;
    private double userBalance;

    public Slots(User user) {
        String gameNameSlots = "Slots";
        double minimumBetSlots = 0.10;

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

            userBalance += resultInBalance(play(),userBet);

            System.out.println("balance actual: "+userBalance);
            System.out.println("Volver a tirar? 1. Sí 2. No");
            int pick;
            pick = tempKey.nextInt();
            tempKey.nextLine();
            if (pick == 2) break;
        }
        if (!canPlay) System.out.println("no podés jugar, pobre");
        return userBalance - fixUserBalance;
    }

    @Override
    public GameResult play() {

        int num1 = (int)Math.round(Math.random()*20);
        int num2 = (int)Math.round(Math.random()*20);
        int num3 = (int)Math.round(Math.random()*20);

        SymbolNode result1 = roll1.getNodes().get(num1);
        SymbolNode result2 = roll2.getNodes().get(num2);
        SymbolNode result3 = roll3.getNodes().get(num3);

        if(result1.prev == null) result1.prev = roll1.getNodes().getLast();
        if(result2.prev == null) result2.prev = roll2.getNodes().getLast();
        if(result3.prev == null) result3.prev = roll3.getNodes().getLast();

        if(result1.next == null) result1.next = roll1.getNodes().getFirst();
        if(result2.next == null) result2.next = roll2.getNodes().getFirst();
        if(result3.next == null) result3.next = roll3.getNodes().getFirst();

        System.out.println("Results: " +
                "Row 1: "+result1.prev.getSymbol()+
                result2.prev.getSymbol()+
                result3.prev.getSymbol());

        System.out.println("Row 2: "+result1.getSymbol()+
                result2.getSymbol()+
                result3.getSymbol());

        System.out.println("Row 3: "+result1.next.getSymbol()+
                result2.next.getSymbol()+
                result3.next.getSymbol());

        if(result1.getSymbol().equals(result2.getSymbol()) && result2.getSymbol().equals(result3.getSymbol())){
            switch (result1.type){
                case 1: return new GameResult(true,3);
                case 2: return new GameResult(true,9);
                case 3: return new GameResult(true,24);
                case 4: return new GameResult(true,199);
            }
        }
        return new GameResult(false,-1);
    }

    @Override
    public double resultInBalance(GameResult result, double betAmount) {

        return betAmount*result.getMultiplier();

    }
}
