package base.mainProgram.Games;

public class GameResult {

    private final boolean isWin;
    private final double multiplier;

    public GameResult (boolean isWin, double multiplier){
        this.isWin = isWin;
        this.multiplier = multiplier;
    }

    public boolean isWin() {
        return isWin;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
