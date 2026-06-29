package mainProgram.Games.Slots;

public class SymbolNode {

    int type;
    SymbolNode prev;
    SymbolNode next;

    public SymbolNode (int type){
        this.type = type;
    }

    public String getSymbol() {
        switch (type){
            case 1: return "Cereza";
            case 2: return "Limón";
            case 3: return "Campana";
            case 4: return "Siete";
            default: return null;
        }
    }

    public SymbolNode getNext() {
        return next;
    }

    public SymbolNode getPrev() {
        return prev;
    }
}
