package base.mainProgram.Games;

public interface Playable {

    boolean hasEnoughMoney (double currentBalance);
    double resultInBalance(GameResult result, double betAmount);

}
