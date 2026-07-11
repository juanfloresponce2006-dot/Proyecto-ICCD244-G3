package base.mainProgram.Games;

import javax.swing.*;

public abstract class Game implements Playable {

    protected final String gameName;
    protected final double minimumBet;

    public Game (String gameName, double minimumBet){
        this.gameName = gameName;
        this.minimumBet = minimumBet;
    }

    public String getGameName (){
        return gameName;
    }

    public abstract GameResult play(JFrame frame);

    @Override
    public boolean hasEnoughMoney (double currentBalance){
        return (currentBalance >= minimumBet);
    }
}
