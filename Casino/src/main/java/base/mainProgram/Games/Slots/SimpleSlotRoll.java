package base.mainProgram.Games.Slots;

import java.util.LinkedList;

public class SimpleSlotRoll {

    private static final LinkedList<SymbolNode> nodes = new LinkedList<>();

    public SimpleSlotRoll(int[] list){
        for (int num : list){
            SymbolNode node = new SymbolNode(num);
            nodes.add(node);
        }
    }

    public LinkedList<SymbolNode> getNodes() {
        return nodes;
    }
}
