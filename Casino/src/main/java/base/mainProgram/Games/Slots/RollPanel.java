/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package base.mainProgram.Games.Slots;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class RollPanel extends JPanel {

    // ---- Configuración fija ----
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private static final int GAP = 5;
    private static final int STEP = ICON_HEIGHT + GAP;   // paso vertical
    private static final int DIRECTION = 10;             // píxeles por frame
    private static final int DELAY = 20;                 // ms entre frames
    private static final int MIN_SPINS = 8;             // vueltas mínimas antes de detenerse
    private static final int PANEL_HEIGHT = 250;         // altura fija del panel

    // ---- Estado ----
    private List<JLabel> labels = new ArrayList<>();
    private int offset = 0;          // desplazamiento vertical en píxeles
    private int spinCounter = 0;     // vueltas completas (cada vez que se rota la lista)

    private Timer timer;
    private Timer pauseTimer;
    private Timer minSpinsTimer;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private JLabel pausingLabel = null;   // label que debe quedar en el centro
    
    private List<RollPanelListener> listeners = new ArrayList<>();

    // ---- Constructor ----
    public RollPanel() {
        initComponents();          // genera el IDE
        setLayout(null);           // posicionamiento absoluto
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(ICON_WIDTH + 20, PANEL_HEIGHT));
        initTimers();
    }

    // ---- Inicialización de timers ----
    private void initTimers() {
        // Timer principal de giro
        timer = new Timer(DELAY, e -> {
            if (isPlaying) advance();
        });
        timer.setCoalesce(true);

        // Timer de pausa: espera a que el label deseado llegue al centro
        pauseTimer = new Timer(DELAY * 2, e -> {
            JLabel centerLabel = getCenterLabel();
            String centerIcon = centerLabel.getIcon().toString();
            String pausingIcon = pausingLabel.getIcon().toString();
            //System.out.println("center label is: "+centerLabel.getIcon()+" pause label is: "+pausingLabel.getIcon());
            if (centerIcon.equals(pausingIcon)) {
                // Centrar exactamente el label deseado
                centerLabelExactly(pausingLabel);
                isPaused = true;
                pauseTimer.stop();
                repaint();
                firePaused();
            } else {
                advance();   // sigue girando hasta que coincida
            }
        });
        pauseTimer.setCoalesce(true);

        // Timer para garantizar un mínimo de vueltas
        minSpinsTimer = new Timer(DELAY * 200, e -> {
            timer.stop();
            pauseTimer.start();
        });
        minSpinsTimer.setCoalesce(true);
    }

    // ---- Lógica de animación ----
    private void advance() {
        if (labels.isEmpty()) return;

        offset += DIRECTION;

        if (offset >= STEP) {
            offset -= STEP;
            // Rotar lista: primer elemento al final
            JLabel first = labels.remove(0);
            labels.add(first);
            spinCounter++;
        } else if (offset < 0) {
            offset += STEP;
            // Rotar al revés: último al principio (por si acaso)
            JLabel last = labels.remove(labels.size() - 1);
            labels.add(0, last);
        }

        // Actualizar posiciones de todos los labels
        int centerX = (getWidth() - ICON_WIDTH) / 2;
        for (int i = 0; i < labels.size(); i++) {
            JLabel label = labels.get(i);
            int y = i * STEP - offset;
            label.setLocation(centerX, y);
        }
        repaint();
    }

    // ---- Centrado exacto de un label ----
    private void centerLabelExactly(JLabel label) {
        int idx = labels.indexOf(label);
        if (idx < 0) return;

        int centerY = getHeight() / 2;
        int halfHeight = ICON_HEIGHT / 2;
        // offset = idx * STEP - (centroY - mitadAlto)

        // Asignar directamente, sin normalizar
        offset = idx * STEP - (centerY - halfHeight);

        // Recalcular posiciones
        int centerX = (getWidth() - ICON_WIDTH) / 2;
        for (int i = 0; i < labels.size(); i++) {
            JLabel lbl = labels.get(i);
            lbl.setLocation(centerX, i * STEP - offset);
        }
    }

    // ---- Métodos públicos ----
    public void addIcon(Icon icon) {
        JLabel label = new JLabel(icon);
        label.setSize(ICON_WIDTH, ICON_HEIGHT);
        labels.add(label);
        add(label);
        // Colocar al final de la lista (la posición se actualizará en el próximo avance)
        int y = (labels.size() - 1) * STEP - offset;
        label.setLocation((getWidth() - ICON_WIDTH) / 2, y);
        repaint();
    }

    public void start() {
        if (labels.isEmpty()) return;
        spinCounter = 0;
        isPlaying = true;
        isPaused = false;
        timer.start();
    }

    public void pauseAt(ImageIcon icon) {
        if (!isPlaying || isPaused) return;
        // Buscar el label que tenga ese icono (comparación por referencia o descripción)
        for (JLabel lbl : labels) {
            if (lbl.getIcon().toString().equals(icon.getDescription())) {
                pausingLabel = lbl;
                break;
            }
        }
        
        if (pausingLabel == null) return;

        if (spinCounter < MIN_SPINS) {
            minSpinsTimer.setRepeats(false);
            minSpinsTimer.start();
        } else {
            timer.stop();
            pauseTimer.start();
        }
    }

    public JLabel getCenterLabel() {
        if (labels.isEmpty()) return null;
        int centerY = getHeight() / 2;
        // El índice del label que cubre el centro (redondeo al más cercano)
        int index = (int) Math.round((centerY + offset) / (double) STEP);
        // Asegurar rango
        index = ((index % labels.size()) + labels.size()) % labels.size();
        return labels.get(index);
    }

    public Icon getCenterIcon() {
        JLabel lbl = getCenterLabel();
        return (lbl != null) ? lbl.getIcon() : null;
    }

    
    public void addRollPanelListener(RollPanelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeRollPanelListener(RollPanelListener listener) {
        listeners.remove(listener);
    }

    private void firePaused() {
        for (RollPanelListener l : listeners) {
            l.onRollPaused();
        }
    }

    // ---- Métodos que ya no se usan pero pueden ser útiles ----
    public boolean isPlaying() { return isPlaying; }
    public boolean isPaused() { return isPaused; }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
