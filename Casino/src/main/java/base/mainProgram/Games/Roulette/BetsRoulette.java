package base.mainProgram.Games.Roulette;

public class BetsRoulette {

    private int type;
    private int[] rangeToCheck;

    public BetsRoulette (int type, int[] rangeToCheck){
        this.rangeToCheck = rangeToCheck;
        this.type = type;
    }

    public int[] getRangeToCheck() {
        return rangeToCheck;
    }

    public int getType() {
        return type;
    }

    public String getTypeString(){
        switch (type){
            case 1: return "Pleno";
            case 2: return "Split";
            case 3: return "Street";
            case 4: return "Square";
            case 5: return "Línea";
            default: return null;
        }
    }

}
