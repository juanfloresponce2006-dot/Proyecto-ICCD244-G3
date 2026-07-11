package base.mainProgram.Games.Slots;

import javax.swing.*;

public class SymbolNode {

    private int type;
    private SymbolNode prev;
    private SymbolNode next;

    public SymbolNode (int type){
        this.type = type;
    }

    public ImageIcon getSymbol() {
        switch (type){
            case 1:
                ImageIcon icon1 = new ImageIcon(getClass().getResource("/base/res/cherries.png"));
                icon1.setDescription("Cherry");
                return icon1;
            case 2: 
                ImageIcon icon2 = new ImageIcon(getClass().getResource("/base/res/lemon.png"));
                icon2.setDescription("Lemon");
                return icon2;
            case 3:
                ImageIcon icon3 = new ImageIcon(getClass().getResource("/base/res/bell.png"));
                icon3.setDescription("Bell");
                return icon3;
            case 4: 
                ImageIcon icon4 = new ImageIcon(getClass().getResource("/base/res/seven.png"));
                icon4.setDescription("Seven");
                return icon4;
            default: return null;
        }
    }

    public int getType(){
        return type;
    }
    
    public SymbolNode getNext() {
        return next;
    }

    public SymbolNode getPrev() {
        return prev;
    }
}
