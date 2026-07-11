/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package base.mainProgram.Games.Roulette;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author User
 */
public class RotatedIcon implements Icon {
    private Icon original;
    private double angle;

    public RotatedIcon(ImageIcon original, double angle) {
        this.original = original;
        this.angle = angle;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = original.getIconWidth();
        int h = original.getIconHeight();
        g2.translate(x + w/2, y + h/2);
        g2.rotate(angle);
        g2.translate(-w/2, -h/2);
        original.paintIcon(c, g2, 0, 0);
        g2.dispose();
    }

    @Override
    public int getIconWidth() { return original.getIconWidth(); }
    @Override
    public int getIconHeight() { return original.getIconHeight(); }
}
