package base.mainProgram.Games.Slots;

import base.accountsHandler.User;
import base.mainProgram.Games.Game;
import base.mainProgram.Games.GameMenu;
import base.mainProgram.Games.GameResult;
import base.mainProgram.Games.Playable;

import javax.swing.*;

public class SlotsConsole extends Game implements Playable {

    private static final int[] numRoll1 = {1,2,1,3,1,2,1,1,4,2,1,3,1,2,1,1,3,2,1,2};
    private static final int[] numRoll2 = {2,1,1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1};
    private static final int[] numRoll3 = {1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1,2,1};

    private final SimpleSlotRoll roll1 = new SimpleSlotRoll(numRoll1);
    private final SimpleSlotRoll roll2 = new SimpleSlotRoll(numRoll2);
    private final SimpleSlotRoll roll3 = new SimpleSlotRoll(numRoll3);

    private double userBalance;
    
    private static final double MIN_BET = 0.10;
    private static final String GAME_NAME = "Slots";

    public SlotsConsole(User user, GameMenu parent) {
        super(GAME_NAME, MIN_BET);
        user.getName();
        this.userBalance = user.getBalance();
    }

    
    public double mainGameFlow (){

        //Scanner tempKey = new Scanner(System.in);

        double userBet;
        boolean canPlay = false;
        
        Slots frame = new Slots(new User(null,null,null,0));
        frame.setVisible(true);
        
        while (hasEnoughMoney(userBalance)){
            canPlay = true;
            
            //System.out.println("Buena suerte, "+userName);
            //System.out.println("Balance: $"+ userBalance);

            /*System.out.println("Cuánto apuestas? mínimo $"+minimumBet);
            while (true){
                try {
                    userBet = tempKey.nextDouble();
                    if (userBet < minimumBet) System.out.println("estás mal de la cabeza? minimo $"+minimumBet);
                    else break;
                }catch (InputMismatchException _){
                    System.out.println("(usa comas we):");
                }
            }*/
        
            
            if(!frame.isVisible()) break;
            String entry = "";
            try{
             userBet = Double.parseDouble(entry);
            }catch(NumberFormatException e){
               userBet = 0;
            }
            if(userBet >= MIN_BET){
                if(!frame.isVisible()) break;
                
            }else {
                JOptionPane.showMessageDialog(frame,"Apuesta mínima: $"+MIN_BET,"Error",JOptionPane.WARNING_MESSAGE);
                continue;
            }

            //tempKey.nextLine();

            if(!frame.isVisible()) break;
            userBalance += resultInBalance(play(frame),userBet);
            
            if(!frame.isVisible()) break;
            
            /*System.out.println("balance actual: "+userBalance);
            System.out.println("Volver a tirar? 1. Sí 2. No");
            int pick;
            pick = tempKey.nextInt();
            tempKey.nextLine();
            if (pick == 2) break;*/
            
            
        }
        //if (!canPlay) System.out.println("no podés jugar, pobre");
        if(!canPlay) JOptionPane.showMessageDialog(frame,"Fondos Insuficientes","Advertencia",JOptionPane.WARNING_MESSAGE);
        return userBalance;
    }


    @Override
    public GameResult play(JFrame frame) {

        int num1 = (int)Math.round(Math.random()*20);
        int num2 = (int)Math.round(Math.random()*20);
        int num3 = (int)Math.round(Math.random()*20);

        SymbolNode result1 = roll1.getNodes().get(num1);
        SymbolNode result2 = roll2.getNodes().get(num2);
        SymbolNode result3 = roll3.getNodes().get(num3);

        

        if(result1.getSymbol().equals(result2.getSymbol()) && result2.getSymbol().equals(result3.getSymbol())){
            switch (result1.getType()){
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
