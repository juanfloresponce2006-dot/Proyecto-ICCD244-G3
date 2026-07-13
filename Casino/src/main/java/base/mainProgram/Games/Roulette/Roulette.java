/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package base.mainProgram.Games.Roulette;

import base.accountsHandler.User;
import base.mainProgram.Games.GameResult;
import base.mainProgram.Games.Playable;
import base.mainProgram.Home;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 *
 * @author User
 */
public class Roulette extends javax.swing.JFrame implements Playable {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Roulette.class.getName());

    private final int[] rojos = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
    private final int[] negros = {2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35};
    
    private final int[] ROULETTE_ORDER = {
        0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5,
        24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    };

    private int typeHandler;
    private final int[] rangeHandler = new int[37];
    
    private final User loggedUser;
    
    private static final double MIN_BET = 1;
    private static final String GAME_NAME = "Roulette";
    
    private static final int DELAY = 50; //20 ms per action
    private static final int BALL_RADIUS = 95;
    private static final double ANGLE_PER_NUMBER = (double)360/37;
    private static final double ZERO_OFFSET = 85;
    private ImageIcon rouletteIcon = new ImageIcon(getClass().getResource("/base/res/roulette2.png"));
    
    private double ballAngle = 0;
    private double rouletteAngle = 0;
    private int resultNum;
    private double result;
    
    private Timer rotationTimer;
    private Timer pauseTimer;
    private Timer minSpinsTimer;
    
    private BufferedImage originalRouletteImage;
    
    private int ballStep = 2;
    private int rouletteStep = 2;
    private static final int ZERO_OFFSET_STEP = -1;
    
    private boolean isSpinning = true;
    
    private JButton selectedButton = null;
    
    private JButton[] botonesNumeros = null;

    /**
     * Creates new form Roulette
     * @param loggedUser
     */
    public Roulette(User loggedUser) {
        initComponents();
        this.loggedUser = loggedUser;
        lblGame.setText(GAME_NAME);
        
        lblRoulette.setIcon(rouletteIcon);
        lblBall.setIcon(new ImageIcon(getClass().getResource("/base/res/ball.png")));
        lblTable.setIcon(new ImageIcon(getClass().getResource("/base/res/rouletteTable.jpg")));
        
        try {
            originalRouletteImage = ImageIO.read(getClass().getResourceAsStream("/base/res/roulette2.png"));
        } catch (IOException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar la imagen de la ruleta", e);
        }
    
        ajustarBotonesApuestas();
        inicializarBotonesApuesta();
        
        initTimers();
        ballAngle = ZERO_OFFSET+ANGLE_PER_NUMBER;
        advanceBall();

        rotationTimer.start();
    }
    
    private void initTimers(){
        rotationTimer = new Timer(DELAY, (ActionEvent e) -> {
            advanceBall();
            ballStep = (ballStep - 2 + 37) % 37;
            advanceRoulette();
            rouletteStep = (rouletteStep + 1) % 37;
        });
        
        pauseTimer = new Timer(DELAY*2, (ActionEvent e) -> {
            advanceBall();
            ballStep = (ballStep - 2 + 37) % 37;
            advanceRoulette();
            rouletteStep = (rouletteStep + 1) % 37;
            if(resultNum == getNumberAt()){
                pauseTimer.stop();
                isSpinning = false;
                
                btnPlay.setEnabled(true);
                updateBalance();
                updateInfoLabel();
                resultNum = -1;
                
            }
 
        });
        
        minSpinsTimer = new Timer(DELAY*50, (ActionEvent e) -> {
            rotationTimer.stop();
            pauseTimer.start();
        });
        
    }
    
    private void advanceBall(){
        int centerX = lblRoulette.getWidth()/2 - lblBall.getWidth()/2;
        int centerY = lblRoulette.getHeight()/2 - lblBall.getHeight()/2;

        ballAngle -= ANGLE_PER_NUMBER*2;

        if(ballAngle <= 0) ballAngle += 360;
        
        double offsetX = BALL_RADIUS*Math.cos(Math.toRadians(ballAngle));
        int x = centerX - (int)Math.round(offsetX);
        
        double offsetY = BALL_RADIUS*Math.sin(Math.toRadians(ballAngle));
        int y = centerY - (int)Math.round(offsetY);
        
        lblBall.setLocation(x, y);
    }
    
    private void advanceRoulette() {
        
        rouletteAngle += (double)ANGLE_PER_NUMBER;
        if (rouletteAngle >= 360) rouletteAngle -= 360;

        int w = lblRoulette.getWidth();
        int h = lblRoulette.getHeight();

        // Crea un BufferedImage del mismo tamaño que el JLabel
        BufferedImage rotatedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // Activa suavizado
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Transformación: rotar alrededor del centro
        AffineTransform at = new AffineTransform();
        at.translate(w / 2.0, h / 2.0);
        at.rotate(Math.toRadians(rouletteAngle));
        at.translate(-w / 2.0, -h / 2.0);

        g2d.drawImage(originalRouletteImage, at, null);
        g2d.dispose();

        // Asigna la imagen rotada al JLabel
        lblRoulette.setIcon(new ImageIcon(rotatedImage));
    }
    
    private int getNumberAt() {
        // Sector relativo de la bola respecto a la ruleta (y al cero)
        int sector = (-ballStep + rouletteStep - ZERO_OFFSET_STEP) % 37;
        if (sector < 0) sector += 37;

        // Como ROULETTE_ORDER está en sentido horario, y la bola se mueve en antihorario,
        // invertimos el índice para obtener el número correcto
        int index = (37 - sector) % 37;
        return ROULETTE_ORDER[index];
    }
    
    public GameResult play(double bet) {

        resultNum = (int)(Math.random() * 37);

        switch (typeHandler){
            case 1:
                if (rangeHandler[0] == resultNum) return new GameResult(true,35);
                break;
            case 2:
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,17);
                }break;
            case 3:
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,11);
                }break;
            case 4  :
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,8);
                }break;
            case 5:
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,5);
                }break;
            case 6:
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,2);
                }break;
            case 7:
                for (int i : rangeHandler){
                    if(i == resultNum) return new GameResult(true,1);
                }break;
        }

        return new GameResult(false,-1);
    }
    
    private void reloadData(){
        NumberFormat twoDecimalsFormat = NumberFormat.getNumberInstance();
        twoDecimalsFormat.setMaximumFractionDigits(2);
        twoDecimalsFormat.setMinimumFractionDigits(2);
        String formattedBalance = twoDecimalsFormat.format(loggedUser.getBalance());
        lblBalance.setText("Balance: $"+formattedBalance);
        lblInfo.setText("");
    }
    
    private void reloadBalance(){
        NumberFormat twoDecimalsFormat = NumberFormat.getNumberInstance();
        twoDecimalsFormat.setMaximumFractionDigits(2);
        twoDecimalsFormat.setMinimumFractionDigits(2);
        String formattedBalance = twoDecimalsFormat.format(loggedUser.getBalance());
        lblBalance.setText("Balance: $"+formattedBalance);
    }
    
    private void updateBalance(){
        loggedUser.setBalance(loggedUser.getBalance()+result);
        reloadBalance();
    }

    private void updateInfoLabel(){
        if(result > 0){
            lblInfo.setBackground(Color.darkGray);
            lblInfo.setForeground(Color.GREEN);
            lblInfo.setText("¡Felicidades, ganaste $"+result+"!");
        }else{
        lblInfo.setBackground(Color.darkGray);
        lblInfo.setForeground(Color.YELLOW);
        lblInfo.setText("Mejor suerte la próxima");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        lblBalance = new javax.swing.JLabel();
        lblGame = new javax.swing.JLabel();
        txtBet = new javax.swing.JTextField();
        btnPlay = new JButton();
        lblInfo = new javax.swing.JLabel();
        panelRoulette = new javax.swing.JPanel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        lblRoulette = new javax.swing.JLabel();
        lblBall = new javax.swing.JLabel();
        panelBets = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        lblTable = new javax.swing.JLabel();
        btn0 = new JButton();
        btn1 = new JButton();
        btn2 = new JButton();
        btn3 = new JButton();
        btn4 = new JButton();
        btn5 = new JButton();
        btn6 = new JButton();
        btn7 = new JButton();
        btn8 = new JButton();
        btn9 = new JButton();
        btn10 = new JButton();
        btn11 = new JButton();
        btn12 = new JButton();
        btn13 = new JButton();
        btn14 = new JButton();
        btn15 = new JButton();
        btn16 = new JButton();
        btn17 = new JButton();
        btn18 = new JButton();
        btn19 = new JButton();
        btn20 = new JButton();
        btn21 = new JButton();
        btn22 = new JButton();
        btn23 = new JButton();
        btn24 = new JButton();
        btn25 = new JButton();
        btn26 = new JButton();
        btn27 = new JButton();
        btn28 = new JButton();
        btn29 = new JButton();
        btn30 = new JButton();
        btn31 = new JButton();
        btn32 = new JButton();
        btn33 = new JButton();
        btn34 = new JButton();
        btn35 = new JButton();
        btn36 = new JButton();
        btnSplit1 = new JButton();
        btnSplit2 = new JButton();
        btnSplit3 = new JButton();
        btnSplit4 = new JButton();
        btnSplit5 = new JButton();
        btnSplit6 = new JButton();
        btnSplit7 = new JButton();
        btnSplit8 = new JButton();
        btnSplit9 = new JButton();
        btnSplit10 = new JButton();
        btnSplit11 = new JButton();
        btnSplit12 = new JButton();
        btnSplit13 = new JButton();
        btnSplit14 = new JButton();
        btnSplit15 = new JButton();
        btnSplit16 = new JButton();
        btnSplit17 = new JButton();
        btnSplit18 = new JButton();
        btnSplit19 = new JButton();
        btnSplit20 = new JButton();
        btnSplit21 = new JButton();
        btnSplit22 = new JButton();
        btnSplit23 = new JButton();
        btnSplit24 = new JButton();
        btnSplit25 = new JButton();
        btnSplit26 = new JButton();
        btnSplit27 = new JButton();
        btnSplit28 = new JButton();
        btnSplit29 = new JButton();
        btnSplit30 = new JButton();
        btnSplit31 = new JButton();
        btnSplit32 = new JButton();
        btnSplit33 = new JButton();
        btnSplit34 = new JButton();
        btnSplit35 = new JButton();
        btnSplit36 = new JButton();
        btnSplit37 = new JButton();
        btnSplit38 = new JButton();
        btnSplit39 = new JButton();
        btnSplit40 = new JButton();
        btnSplit41 = new JButton();
        btnSplit42 = new JButton();
        btnSplit43 = new JButton();
        btnSplit44 = new JButton();
        btnSplit45 = new JButton();
        btnSplit46 = new JButton();
        btnSplit47 = new JButton();
        btnSplit48 = new JButton();
        btnSplit49 = new JButton();
        btnSplit50 = new JButton();
        btnSplit51 = new JButton();
        btnSplit52 = new JButton();
        btnSplit53 = new JButton();
        btnSplit54 = new JButton();
        btnSplit55 = new JButton();
        btnSplit56 = new JButton();
        btnSplit57 = new JButton();
        btnStreet1 = new JButton();
        btnStreet2 = new JButton();
        btnStreet3 = new JButton();
        btnStreet4 = new JButton();
        btnStreet5 = new JButton();
        btnStreet6 = new JButton();
        btnStreet7 = new JButton();
        btnStreet8 = new JButton();
        btnStreet9 = new JButton();
        btnStreet10 = new JButton();
        btnStreet11 = new JButton();
        btnStreet12 = new JButton();
        btnSquare1 = new JButton();
        btnSquare2 = new JButton();
        btnSquare3 = new JButton();
        btnSquare4 = new JButton();
        btnSquare5 = new JButton();
        btnSquare6 = new JButton();
        btnSquare7 = new JButton();
        btnSquare8 = new JButton();
        btnSquare9 = new JButton();
        btnSquare10 = new JButton();
        btnSquare11 = new JButton();
        btnSquare12 = new JButton();
        btnSquare13 = new JButton();
        btnSquare14 = new JButton();
        btnSquare15 = new JButton();
        btnSquare16 = new JButton();
        btnSquare17 = new JButton();
        btnSquare18 = new JButton();
        btnSquare19 = new JButton();
        btnSquare20 = new JButton();
        btnSquare21 = new JButton();
        btnSquare22 = new JButton();
        btnLane1 = new JButton();
        btnLane2 = new JButton();
        btnLane3 = new JButton();
        btnLane4 = new JButton();
        btnLane5 = new JButton();
        btnLane6 = new JButton();
        btnLane7 = new JButton();
        btnLane8 = new JButton();
        btnLane9 = new JButton();
        btnLane10 = new JButton();
        btnLane11 = new JButton();
        btn1st12 = new JButton();
        btn2nd12 = new JButton();
        btn3rd12 = new JButton();
        btnColumn1 = new JButton();
        btnColumn2 = new JButton();
        btnColumn3 = new JButton();
        btn1st18 = new JButton();
        btn2nd18 = new JButton();
        btnEven = new JButton();
        btnOdd = new JButton();
        btnRed = new JButton();
        btnBlack = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setMinimumSize(new java.awt.Dimension(500, 600));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 600));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblBalance.setText("Balance: $00.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        jPanel1.add(lblBalance, gridBagConstraints);

        lblGame.setText("Roulette Game");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 75, 0);
        jPanel1.add(lblGame, gridBagConstraints);

        txtBet.setColumns(10);
        txtBet.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        jPanel1.add(txtBet, gridBagConstraints);

        btnPlay.setText("Jugar!");
        btnPlay.addActionListener(this::btnPlayActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        jPanel1.add(btnPlay, gridBagConstraints);

        lblInfo.setBackground(new Color(51, 51, 51));
        lblInfo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblInfo.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        jPanel1.add(lblInfo, gridBagConstraints);

        panelRoulette.setMinimumSize(new java.awt.Dimension(300, 300));
        panelRoulette.setPreferredSize(new java.awt.Dimension(300, 300));
        panelRoulette.setLayout(null);

        jLayeredPane2.setMinimumSize(new java.awt.Dimension(300, 300));
        jLayeredPane2.setPreferredSize(new java.awt.Dimension(300, 300));

        lblRoulette.setMaximumSize(new java.awt.Dimension(300, 300));
        lblRoulette.setMinimumSize(new java.awt.Dimension(300, 300));
        lblRoulette.setPreferredSize(new java.awt.Dimension(300, 300));
        jLayeredPane2.setLayer(lblRoulette, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane2.add(lblRoulette);
        lblRoulette.setBounds(0, 0, 300, 300);

        lblBall.setBackground(new Color(102, 204, 0));
        lblBall.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBall.setMaximumSize(new java.awt.Dimension(15, 15));
        lblBall.setMinimumSize(new java.awt.Dimension(15, 15));
        lblBall.setPreferredSize(new java.awt.Dimension(15, 15));
        jLayeredPane2.setLayer(lblBall, javax.swing.JLayeredPane.DRAG_LAYER);
        jLayeredPane2.add(lblBall);
        lblBall.setBounds(145, 145, 15, 15);

        panelRoulette.add(jLayeredPane2);
        jLayeredPane2.setBounds(0, 0, 300, 300);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        jPanel1.add(panelRoulette, gridBagConstraints);

        panelBets.setPreferredSize(new java.awt.Dimension(570, 300));

        lblTable.setBackground(new Color(255, 51, 51));
        lblTable.setMaximumSize(new java.awt.Dimension(570, 300));
        lblTable.setMinimumSize(new java.awt.Dimension(570, 300));
        lblTable.setOpaque(true);
        lblTable.setPreferredSize(new java.awt.Dimension(570, 300));
        jLayeredPane1.add(lblTable);
        lblTable.setBounds(0, 0, 570, 300);

        btn0.setBorder(null);
        btn0.setBorderPainted(false);
        btn0.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn0.setOpaque(false);
        jLayeredPane1.setLayer(btn0, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn0);
        btn0.setBounds(23, 97, 20, 20);

        btn1.setBorder(null);
        btn1.setBorderPainted(false);
        btn1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn1);
        btn1.setBounds(62, 155, 20, 20);

        btn2.setBorder(null);
        btn2.setBorderPainted(false);
        btn2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn2);
        btn2.setBounds(62, 97, 20, 20);

        btn3.setBorder(null);
        btn3.setBorderPainted(false);
        btn3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn3);
        btn3.setBounds(62, 40, 20, 20);

        btn4.setBorder(null);
        btn4.setBorderPainted(false);
        btn4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn4, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn4);
        btn4.setBounds(100, 155, 20, 20);

        btn5.setBorder(null);
        btn5.setBorderPainted(false);
        btn5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn5, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn5);
        btn5.setBounds(100, 97, 20, 20);

        btn6.setBorder(null);
        btn6.setBorderPainted(false);
        btn6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn6, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn6);
        btn6.setBounds(100, 40, 20, 20);

        btn7.setBorder(null);
        btn7.setBorderPainted(false);
        btn7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn7, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn7);
        btn7.setBounds(138, 155, 20, 20);

        btn8.setBorder(null);
        btn8.setBorderPainted(false);
        btn8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn8, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn8);
        btn8.setBounds(138, 97, 20, 20);

        btn9.setBorder(null);
        btn9.setBorderPainted(false);
        btn9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn9, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn9);
        btn9.setBounds(138, 40, 20, 20);

        btn10.setBorder(null);
        btn10.setBorderPainted(false);
        btn10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn10, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn10);
        btn10.setBounds(178, 155, 20, 20);

        btn11.setBorder(null);
        btn11.setBorderPainted(false);
        btn11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn11, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn11);
        btn11.setBounds(178, 97, 20, 20);

        btn12.setBorder(null);
        btn12.setBorderPainted(false);
        btn12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn12);
        btn12.setBounds(178, 40, 20, 20);

        btn13.setBorder(null);
        btn13.setBorderPainted(false);
        btn13.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn13, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn13);
        btn13.setBounds(217, 155, 20, 20);

        btn14.setBorder(null);
        btn14.setBorderPainted(false);
        btn14.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn14, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn14);
        btn14.setBounds(217, 97, 20, 20);

        btn15.setBorder(null);
        btn15.setBorderPainted(false);
        btn15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn15, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn15);
        btn15.setBounds(217, 40, 20, 20);

        btn16.setBorder(null);
        btn16.setBorderPainted(false);
        btn16.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn16, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn16);
        btn16.setBounds(255, 155, 20, 20);

        btn17.setBorder(null);
        btn17.setBorderPainted(false);
        btn17.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn17, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn17);
        btn17.setBounds(255, 97, 20, 20);

        btn18.setBorder(null);
        btn18.setBorderPainted(false);
        btn18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn18, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn18);
        btn18.setBounds(255, 40, 20, 20);

        btn19.setBorder(null);
        btn19.setBorderPainted(false);
        btn19.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn19, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn19);
        btn19.setBounds(293, 155, 20, 20);

        btn20.setBorder(null);
        btn20.setBorderPainted(false);
        btn20.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn20, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn20);
        btn20.setBounds(293, 97, 20, 20);

        btn21.setBorder(null);
        btn21.setBorderPainted(false);
        btn21.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn21, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn21);
        btn21.setBounds(293, 40, 20, 20);

        btn22.setBorder(null);
        btn22.setBorderPainted(false);
        btn22.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn22, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn22);
        btn22.setBounds(332, 155, 20, 20);

        btn23.setBorder(null);
        btn23.setBorderPainted(false);
        btn23.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn23, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn23);
        btn23.setBounds(332, 97, 20, 20);

        btn24.setBorder(null);
        btn24.setBorderPainted(false);
        btn24.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn24, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn24);
        btn24.setBounds(332, 40, 20, 20);

        btn25.setBorder(null);
        btn25.setBorderPainted(false);
        btn25.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn25, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn25);
        btn25.setBounds(372, 155, 20, 20);

        btn26.setBorder(null);
        btn26.setBorderPainted(false);
        btn26.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn26, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn26);
        btn26.setBounds(372, 97, 20, 20);

        btn27.setBorder(null);
        btn27.setBorderPainted(false);
        btn27.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn27, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn27);
        btn27.setBounds(372, 40, 20, 20);

        btn28.setBorder(null);
        btn28.setBorderPainted(false);
        btn28.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn28, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn28);
        btn28.setBounds(410, 155, 20, 20);

        btn29.setBorder(null);
        btn29.setBorderPainted(false);
        btn29.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn29, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn29);
        btn29.setBounds(410, 97, 20, 20);

        btn30.setBorder(null);
        btn30.setBorderPainted(false);
        btn30.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn30, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn30);
        btn30.setBounds(410, 40, 20, 20);

        btn31.setBorder(null);
        btn31.setBorderPainted(false);
        btn31.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn31, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn31);
        btn31.setBounds(448, 155, 20, 20);

        btn32.setBorder(null);
        btn32.setBorderPainted(false);
        btn32.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn32, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn32);
        btn32.setBounds(448, 97, 20, 20);

        btn33.setBorder(null);
        btn33.setBorderPainted(false);
        btn33.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn33, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn33);
        btn33.setBounds(448, 40, 20, 20);

        btn34.setBorder(null);
        btn34.setBorderPainted(false);
        btn34.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn34, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn34);
        btn34.setBounds(487, 155, 20, 20);

        btn35.setBorder(null);
        btn35.setBorderPainted(false);
        btn35.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn35, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn35);
        btn35.setBounds(487, 97, 20, 20);

        btn36.setBorder(null);
        btn36.setBorderPainted(false);
        btn36.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn36, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn36);
        btn36.setBounds(487, 40, 20, 20);

        btnSplit1.setBorder(null);
        btnSplit1.setBorderPainted(false);
        btnSplit1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit1);
        btnSplit1.setBounds(60, 130, 20, 20);

        btnSplit2.setBorder(null);
        btnSplit2.setBorderPainted(false);
        btnSplit2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit2);
        btnSplit2.setBounds(60, 70, 20, 20);

        btnSplit3.setBorder(null);
        btnSplit3.setBorderPainted(false);
        btnSplit3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit3);
        btnSplit3.setBounds(80, 160, 20, 20);

        btnSplit4.setBorder(null);
        btnSplit4.setBorderPainted(false);
        btnSplit4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit4, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit4);
        btnSplit4.setBounds(80, 100, 20, 20);

        btnSplit5.setBorder(null);
        btnSplit5.setBorderPainted(false);
        btnSplit5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit5, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit5);
        btnSplit5.setBounds(80, 40, 20, 20);

        btnSplit6.setBorder(null);
        btnSplit6.setBorderPainted(false);
        btnSplit6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit6, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit6);
        btnSplit6.setBounds(100, 130, 20, 20);

        btnSplit7.setBorder(null);
        btnSplit7.setBorderPainted(false);
        btnSplit7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit7, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit7);
        btnSplit7.setBounds(100, 70, 20, 20);

        btnSplit8.setBorder(null);
        btnSplit8.setBorderPainted(false);
        btnSplit8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit8, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit8);
        btnSplit8.setBounds(120, 150, 20, 20);

        btnSplit9.setBorder(null);
        btnSplit9.setBorderPainted(false);
        btnSplit9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit9, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit9);
        btnSplit9.setBounds(120, 100, 20, 20);

        btnSplit10.setBorder(null);
        btnSplit10.setBorderPainted(false);
        btnSplit10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit10, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit10);
        btnSplit10.setBounds(120, 40, 20, 20);

        btnSplit11.setBorder(null);
        btnSplit11.setBorderPainted(false);
        btnSplit11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit11, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit11);
        btnSplit11.setBounds(140, 130, 20, 20);

        btnSplit12.setBorder(null);
        btnSplit12.setBorderPainted(false);
        btnSplit12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit12);
        btnSplit12.setBounds(140, 70, 20, 20);

        btnSplit13.setBorder(null);
        btnSplit13.setBorderPainted(false);
        btnSplit13.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit13, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit13);
        btnSplit13.setBounds(160, 150, 20, 20);

        btnSplit14.setBorder(null);
        btnSplit14.setBorderPainted(false);
        btnSplit14.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit14, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit14);
        btnSplit14.setBounds(160, 100, 20, 20);

        btnSplit15.setBorder(null);
        btnSplit15.setBorderPainted(false);
        btnSplit15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit15, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit15);
        btnSplit15.setBounds(160, 40, 20, 20);

        btnSplit16.setBorder(null);
        btnSplit16.setBorderPainted(false);
        btnSplit16.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit16, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit16);
        btnSplit16.setBounds(180, 130, 20, 20);

        btnSplit17.setBorder(null);
        btnSplit17.setBorderPainted(false);
        btnSplit17.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit17, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit17);
        btnSplit17.setBounds(180, 70, 20, 20);

        btnSplit18.setBorder(null);
        btnSplit18.setBorderPainted(false);
        btnSplit18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit18, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit18);
        btnSplit18.setBounds(200, 160, 20, 20);

        btnSplit19.setBorder(null);
        btnSplit19.setBorderPainted(false);
        btnSplit19.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit19, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit19);
        btnSplit19.setBounds(200, 100, 20, 20);

        btnSplit20.setBorder(null);
        btnSplit20.setBorderPainted(false);
        btnSplit20.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit20, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit20);
        btnSplit20.setBounds(200, 40, 20, 20);

        btnSplit21.setBorder(null);
        btnSplit21.setBorderPainted(false);
        btnSplit21.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit21, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit21);
        btnSplit21.setBounds(220, 130, 20, 20);

        btnSplit22.setBorder(null);
        btnSplit22.setBorderPainted(false);
        btnSplit22.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit22, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit22);
        btnSplit22.setBounds(220, 70, 20, 20);

        btnSplit23.setBorder(null);
        btnSplit23.setBorderPainted(false);
        btnSplit23.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit23, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit23);
        btnSplit23.setBounds(240, 160, 20, 20);

        btnSplit24.setBorder(null);
        btnSplit24.setBorderPainted(false);
        btnSplit24.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit24, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit24);
        btnSplit24.setBounds(240, 100, 20, 20);

        btnSplit25.setBorder(null);
        btnSplit25.setBorderPainted(false);
        btnSplit25.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit25, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit25);
        btnSplit25.setBounds(240, 40, 20, 20);

        btnSplit26.setBorder(null);
        btnSplit26.setBorderPainted(false);
        btnSplit26.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit26, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit26);
        btnSplit26.setBounds(250, 130, 20, 20);

        btnSplit27.setBorder(null);
        btnSplit27.setBorderPainted(false);
        btnSplit27.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit27, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit27);
        btnSplit27.setBounds(250, 70, 20, 20);

        btnSplit28.setBorder(null);
        btnSplit28.setBorderPainted(false);
        btnSplit28.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit28, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit28);
        btnSplit28.setBounds(270, 150, 20, 20);

        btnSplit29.setBorder(null);
        btnSplit29.setBorderPainted(false);
        btnSplit29.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit29, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit29);
        btnSplit29.setBounds(280, 100, 20, 20);

        btnSplit30.setBorder(null);
        btnSplit30.setBorderPainted(false);
        btnSplit30.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit30, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit30);
        btnSplit30.setBounds(280, 40, 20, 20);

        btnSplit31.setBorder(null);
        btnSplit31.setBorderPainted(false);
        btnSplit31.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit31, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit31);
        btnSplit31.setBounds(290, 130, 20, 20);

        btnSplit32.setBorder(null);
        btnSplit32.setBorderPainted(false);
        btnSplit32.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit32, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit32);
        btnSplit32.setBounds(290, 70, 20, 20);

        btnSplit33.setBorder(null);
        btnSplit33.setBorderPainted(false);
        btnSplit33.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit33, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit33);
        btnSplit33.setBounds(310, 160, 20, 20);

        btnSplit34.setBorder(null);
        btnSplit34.setBorderPainted(false);
        btnSplit34.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit34, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit34);
        btnSplit34.setBounds(320, 100, 20, 20);

        btnSplit35.setBorder(null);
        btnSplit35.setBorderPainted(false);
        btnSplit35.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit35, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit35);
        btnSplit35.setBounds(310, 40, 20, 20);

        btnSplit36.setBorder(null);
        btnSplit36.setBorderPainted(false);
        btnSplit36.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit36, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit36);
        btnSplit36.setBounds(330, 130, 20, 20);

        btnSplit37.setBorder(null);
        btnSplit37.setBorderPainted(false);
        btnSplit37.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit37, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit37);
        btnSplit37.setBounds(330, 70, 20, 20);

        btnSplit38.setBorder(null);
        btnSplit38.setBorderPainted(false);
        btnSplit38.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit38, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit38);
        btnSplit38.setBounds(360, 160, 20, 20);

        btnSplit39.setBorder(null);
        btnSplit39.setBorderPainted(false);
        btnSplit39.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit39, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit39);
        btnSplit39.setBounds(350, 100, 20, 20);

        btnSplit40.setBorder(null);
        btnSplit40.setBorderPainted(false);
        btnSplit40.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit40, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit40);
        btnSplit40.setBounds(350, 40, 20, 20);

        btnSplit41.setBorder(null);
        btnSplit41.setBorderPainted(false);
        btnSplit41.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit41, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit41);
        btnSplit41.setBounds(370, 130, 20, 20);

        btnSplit42.setBorder(null);
        btnSplit42.setBorderPainted(false);
        btnSplit42.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit42, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit42);
        btnSplit42.setBounds(370, 70, 20, 20);

        btnSplit43.setBorder(null);
        btnSplit43.setBorderPainted(false);
        btnSplit43.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit43, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit43);
        btnSplit43.setBounds(390, 160, 20, 20);

        btnSplit44.setBorder(null);
        btnSplit44.setBorderPainted(false);
        btnSplit44.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit44, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit44);
        btnSplit44.setBounds(390, 100, 20, 20);

        btnSplit45.setBorder(null);
        btnSplit45.setBorderPainted(false);
        btnSplit45.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit45, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit45);
        btnSplit45.setBounds(390, 40, 20, 20);

        btnSplit46.setBorder(null);
        btnSplit46.setBorderPainted(false);
        btnSplit46.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit46, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit46);
        btnSplit46.setBounds(410, 130, 20, 20);

        btnSplit47.setBorder(null);
        btnSplit47.setBorderPainted(false);
        btnSplit47.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit47, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit47);
        btnSplit47.setBounds(410, 70, 20, 20);

        btnSplit48.setBorder(null);
        btnSplit48.setBorderPainted(false);
        btnSplit48.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit48, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit48);
        btnSplit48.setBounds(430, 160, 20, 20);

        btnSplit49.setBorder(null);
        btnSplit49.setBorderPainted(false);
        btnSplit49.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit49, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit49);
        btnSplit49.setBounds(430, 100, 20, 20);

        btnSplit50.setBorder(null);
        btnSplit50.setBorderPainted(false);
        btnSplit50.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit50, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit50);
        btnSplit50.setBounds(430, 40, 20, 20);

        btnSplit51.setBorder(null);
        btnSplit51.setBorderPainted(false);
        btnSplit51.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit51, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit51);
        btnSplit51.setBounds(450, 130, 20, 20);

        btnSplit52.setBorder(null);
        btnSplit52.setBorderPainted(false);
        btnSplit52.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit52, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit52);
        btnSplit52.setBounds(450, 70, 20, 20);

        btnSplit53.setBorder(null);
        btnSplit53.setBorderPainted(false);
        btnSplit53.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit53, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit53);
        btnSplit53.setBounds(470, 160, 20, 20);

        btnSplit54.setBorder(null);
        btnSplit54.setBorderPainted(false);
        btnSplit54.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit54, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit54);
        btnSplit54.setBounds(470, 100, 20, 20);

        btnSplit55.setBorder(null);
        btnSplit55.setBorderPainted(false);
        btnSplit55.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit55, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit55);
        btnSplit55.setBounds(470, 40, 20, 20);

        btnSplit56.setBorder(null);
        btnSplit56.setBorderPainted(false);
        btnSplit56.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit56, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit56);
        btnSplit56.setBounds(490, 130, 20, 20);

        btnSplit57.setBorder(null);
        btnSplit57.setBorderPainted(false);
        btnSplit57.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSplit57, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSplit57);
        btnSplit57.setBounds(490, 70, 20, 20);

        btnStreet1.setBorder(null);
        btnStreet1.setBorderPainted(false);
        btnStreet1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet1);
        btnStreet1.setBounds(60, 10, 20, 20);

        btnStreet2.setBorder(null);
        btnStreet2.setBorderPainted(false);
        btnStreet2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet2);
        btnStreet2.setBounds(100, 10, 20, 20);

        btnStreet3.setBorder(null);
        btnStreet3.setBorderPainted(false);
        btnStreet3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet3);
        btnStreet3.setBounds(140, 10, 20, 20);

        btnStreet4.setBorder(null);
        btnStreet4.setBorderPainted(false);
        btnStreet4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet4, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet4);
        btnStreet4.setBounds(180, 10, 20, 20);

        btnStreet5.setBorder(null);
        btnStreet5.setBorderPainted(false);
        btnStreet5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet5, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet5);
        btnStreet5.setBounds(220, 10, 20, 20);

        btnStreet6.setBorder(null);
        btnStreet6.setBorderPainted(false);
        btnStreet6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet6, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet6);
        btnStreet6.setBounds(260, 10, 20, 20);

        btnStreet7.setBorder(null);
        btnStreet7.setBorderPainted(false);
        btnStreet7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet7, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet7);
        btnStreet7.setBounds(290, 10, 20, 20);

        btnStreet8.setBorder(null);
        btnStreet8.setBorderPainted(false);
        btnStreet8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet8, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet8);
        btnStreet8.setBounds(330, 10, 20, 20);

        btnStreet9.setBorder(null);
        btnStreet9.setBorderPainted(false);
        btnStreet9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet9, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet9);
        btnStreet9.setBounds(370, 10, 20, 20);

        btnStreet10.setBorder(null);
        btnStreet10.setBorderPainted(false);
        btnStreet10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet10, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet10);
        btnStreet10.setBounds(410, 10, 20, 20);

        btnStreet11.setBorder(null);
        btnStreet11.setBorderPainted(false);
        btnStreet11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet11, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet11);
        btnStreet11.setBounds(450, 10, 20, 20);

        btnStreet12.setBorder(null);
        btnStreet12.setBorderPainted(false);
        btnStreet12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnStreet12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnStreet12);
        btnStreet12.setBounds(490, 10, 20, 20);

        btnSquare1.setBorder(null);
        btnSquare1.setBorderPainted(false);
        btnSquare1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare1);
        btnSquare1.setBounds(80, 130, 20, 20);

        btnSquare2.setBorder(null);
        btnSquare2.setBorderPainted(false);
        btnSquare2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare2);
        btnSquare2.setBounds(80, 70, 20, 20);

        btnSquare3.setBorder(null);
        btnSquare3.setBorderPainted(false);
        btnSquare3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare3);
        btnSquare3.setBounds(120, 130, 20, 20);

        btnSquare4.setBorder(null);
        btnSquare4.setBorderPainted(false);
        btnSquare4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare4, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare4);
        btnSquare4.setBounds(120, 70, 20, 20);

        btnSquare5.setBorder(null);
        btnSquare5.setBorderPainted(false);
        btnSquare5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare5, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare5);
        btnSquare5.setBounds(160, 130, 20, 20);

        btnSquare6.setBorder(null);
        btnSquare6.setBorderPainted(false);
        btnSquare6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare6, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare6);
        btnSquare6.setBounds(160, 70, 20, 20);

        btnSquare7.setBorder(null);
        btnSquare7.setBorderPainted(false);
        btnSquare7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare7, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare7);
        btnSquare7.setBounds(200, 130, 20, 20);

        btnSquare8.setBorder(null);
        btnSquare8.setBorderPainted(false);
        btnSquare8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare8, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare8);
        btnSquare8.setBounds(200, 70, 20, 20);

        btnSquare9.setBorder(null);
        btnSquare9.setBorderPainted(false);
        btnSquare9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare9, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare9);
        btnSquare9.setBounds(240, 130, 20, 20);

        btnSquare10.setBorder(null);
        btnSquare10.setBorderPainted(false);
        btnSquare10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare10, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare10);
        btnSquare10.setBounds(240, 70, 20, 20);

        btnSquare11.setBorder(null);
        btnSquare11.setBorderPainted(false);
        btnSquare11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare11, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare11);
        btnSquare11.setBounds(270, 130, 20, 20);

        btnSquare12.setBorder(null);
        btnSquare12.setBorderPainted(false);
        btnSquare12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare12);
        btnSquare12.setBounds(270, 70, 20, 20);

        btnSquare13.setBorder(null);
        btnSquare13.setBorderPainted(false);
        btnSquare13.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare13, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare13);
        btnSquare13.setBounds(310, 130, 20, 20);

        btnSquare14.setBorder(null);
        btnSquare14.setBorderPainted(false);
        btnSquare14.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare14, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare14);
        btnSquare14.setBounds(310, 70, 20, 20);

        btnSquare15.setBorder(null);
        btnSquare15.setBorderPainted(false);
        btnSquare15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare15, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare15);
        btnSquare15.setBounds(350, 130, 20, 20);

        btnSquare16.setBorder(null);
        btnSquare16.setBorderPainted(false);
        btnSquare16.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare16, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare16);
        btnSquare16.setBounds(350, 70, 20, 20);

        btnSquare17.setBorder(null);
        btnSquare17.setBorderPainted(false);
        btnSquare17.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare17, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare17);
        btnSquare17.setBounds(390, 130, 20, 20);

        btnSquare18.setBorder(null);
        btnSquare18.setBorderPainted(false);
        btnSquare18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare18, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare18);
        btnSquare18.setBounds(390, 70, 20, 20);

        btnSquare19.setBorder(null);
        btnSquare19.setBorderPainted(false);
        btnSquare19.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare19, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare19);
        btnSquare19.setBounds(430, 130, 20, 20);

        btnSquare20.setBorder(null);
        btnSquare20.setBorderPainted(false);
        btnSquare20.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare20, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare20);
        btnSquare20.setBounds(430, 70, 20, 20);

        btnSquare21.setBorder(null);
        btnSquare21.setBorderPainted(false);
        btnSquare21.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare21, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare21);
        btnSquare21.setBounds(470, 130, 20, 20);

        btnSquare22.setBorder(null);
        btnSquare22.setBorderPainted(false);
        btnSquare22.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnSquare22, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnSquare22);
        btnSquare22.setBounds(470, 70, 20, 20);

        btnLane1.setBorder(null);
        btnLane1.setBorderPainted(false);
        btnLane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane1);
        btnLane1.setBounds(80, 180, 20, 20);

        btnLane2.setBorder(null);
        btnLane2.setBorderPainted(false);
        btnLane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane2);
        btnLane2.setBounds(120, 180, 20, 20);

        btnLane3.setBorder(null);
        btnLane3.setBorderPainted(false);
        btnLane3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane3);
        btnLane3.setBounds(160, 180, 20, 20);

        btnLane4.setBorder(null);
        btnLane4.setBorderPainted(false);
        btnLane4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane4, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane4);
        btnLane4.setBounds(200, 180, 20, 20);

        btnLane5.setBorder(null);
        btnLane5.setBorderPainted(false);
        btnLane5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane5, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane5);
        btnLane5.setBounds(240, 180, 20, 20);

        btnLane6.setBorder(null);
        btnLane6.setBorderPainted(false);
        btnLane6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane6, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane6);
        btnLane6.setBounds(280, 180, 20, 20);

        btnLane7.setBorder(null);
        btnLane7.setBorderPainted(false);
        btnLane7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane7, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane7);
        btnLane7.setBounds(310, 180, 20, 20);

        btnLane8.setBorder(null);
        btnLane8.setBorderPainted(false);
        btnLane8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane8, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane8);
        btnLane8.setBounds(350, 180, 20, 20);

        btnLane9.setBorder(null);
        btnLane9.setBorderPainted(false);
        btnLane9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane9, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane9);
        btnLane9.setBounds(390, 180, 20, 20);

        btnLane10.setBorder(null);
        btnLane10.setBorderPainted(false);
        btnLane10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane10, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane10);
        btnLane10.setBounds(430, 180, 20, 20);

        btnLane11.setBorder(null);
        btnLane11.setBorderPainted(false);
        btnLane11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnLane11, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnLane11);
        btnLane11.setBounds(470, 180, 20, 20);

        btn1st12.setBorder(null);
        btn1st12.setBorderPainted(false);
        btn1st12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn1st12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn1st12);
        btn1st12.setBounds(120, 210, 20, 20);

        btn2nd12.setBorder(null);
        btn2nd12.setBorderPainted(false);
        btn2nd12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn2nd12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn2nd12);
        btn2nd12.setBounds(280, 210, 20, 20);

        btn3rd12.setBorder(null);
        btn3rd12.setBorderPainted(false);
        btn3rd12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn3rd12, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn3rd12);
        btn3rd12.setBounds(430, 210, 20, 20);

        btnColumn1.setBorder(null);
        btnColumn1.setBorderPainted(false);
        btnColumn1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnColumn1, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnColumn1);
        btnColumn1.setBounds(530, 160, 20, 20);

        btnColumn2.setBorder(null);
        btnColumn2.setBorderPainted(false);
        btnColumn2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnColumn2, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnColumn2);
        btnColumn2.setBounds(530, 100, 20, 20);

        btnColumn3.setBorder(null);
        btnColumn3.setBorderPainted(false);
        btnColumn3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnColumn3, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnColumn3);
        btnColumn3.setBounds(530, 40, 20, 20);

        btn1st18.setBorder(null);
        btn1st18.setBorderPainted(false);
        btn1st18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn1st18, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn1st18);
        btn1st18.setBounds(80, 250, 20, 20);

        btn2nd18.setBorder(null);
        btn2nd18.setBorderPainted(false);
        btn2nd18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btn2nd18, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btn2nd18);
        btn2nd18.setBounds(470, 250, 20, 20);

        btnEven.setBorder(null);
        btnEven.setBorderPainted(false);
        btnEven.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnEven, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnEven);
        btnEven.setBounds(160, 250, 20, 20);

        btnOdd.setBorder(null);
        btnOdd.setBorderPainted(false);
        btnOdd.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnOdd, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnOdd);
        btnOdd.setBounds(390, 250, 20, 20);

        btnRed.setBorder(null);
        btnRed.setBorderPainted(false);
        btnRed.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnRed, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnRed);
        btnRed.setBounds(240, 250, 20, 20);

        btnBlack.setBorder(null);
        btnBlack.setBorderPainted(false);
        btnBlack.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLayeredPane1.setLayer(btnBlack, javax.swing.JLayeredPane.MODAL_LAYER);
        jLayeredPane1.add(btnBlack);
        btnBlack.setBounds(310, 250, 20, 20);

        javax.swing.GroupLayout panelBetsLayout = new javax.swing.GroupLayout(panelBets);
        panelBets.setLayout(panelBetsLayout);
        panelBetsLayout.setHorizontalGroup(
            panelBetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelBetsLayout.setVerticalGroup(
            panelBetsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        jPanel1.add(panelBets, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 953, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.dispose();
        Home home = new Home(loggedUser);
        home.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void btnPlayActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed

        if(typeHandler == 0){
            JOptionPane.showMessageDialog(this,"Haz click en la mesa de apuestas","Advertencia",JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double bet;
        try{
            bet = Double.parseDouble(txtBet.getText());
            
            if(bet >= MIN_BET){
                if(bet <= loggedUser.getBalance()){

                    if(isSpinning){
                        rotationTimer.stop();
                        pauseTimer.start();
                    }else{
                        rotationTimer.start();
                        minSpinsTimer.setRepeats(false);
                        minSpinsTimer.start();
                        isSpinning = true;
                    }
                    
                    result = resultInBalance(play(bet),bet);

                    typeHandler = 0;
                    deseleccionarTodos();
                    btnPlay.setEnabled(false);
                }else{
                    JOptionPane.showMessageDialog(this,"Saldo insuficiente","Advertencia",JOptionPane.WARNING_MESSAGE);
                }
            }else {
                JOptionPane.showMessageDialog(this,"Apuesta mínima: $"+MIN_BET,"Advertencia",JOptionPane.WARNING_MESSAGE);
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this,"Ingrese solo números","Error",JOptionPane.WARNING_MESSAGE);
        }
        
        reloadData();
    }//GEN-LAST:event_btnPlayActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btn0;
    private JButton btn1;
    private JButton btn10;
    private JButton btn11;
    private JButton btn12;
    private JButton btn13;
    private JButton btn14;
    private JButton btn15;
    private JButton btn16;
    private JButton btn17;
    private JButton btn18;
    private JButton btn19;
    private JButton btn1st12;
    private JButton btn1st18;
    private JButton btn2;
    private JButton btn20;
    private JButton btn21;
    private JButton btn22;
    private JButton btn23;
    private JButton btn24;
    private JButton btn25;
    private JButton btn26;
    private JButton btn27;
    private JButton btn28;
    private JButton btn29;
    private JButton btn2nd12;
    private JButton btn2nd18;
    private JButton btn3;
    private JButton btn30;
    private JButton btn31;
    private JButton btn32;
    private JButton btn33;
    private JButton btn34;
    private JButton btn35;
    private JButton btn36;
    private JButton btn3rd12;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JButton btn7;
    private JButton btn8;
    private JButton btn9;
    private JButton btnBlack;
    private JButton btnColumn1;
    private JButton btnColumn2;
    private JButton btnColumn3;
    private JButton btnEven;
    private JButton btnLane1;
    private JButton btnLane10;
    private JButton btnLane11;
    private JButton btnLane2;
    private JButton btnLane3;
    private JButton btnLane4;
    private JButton btnLane5;
    private JButton btnLane6;
    private JButton btnLane7;
    private JButton btnLane8;
    private JButton btnLane9;
    private JButton btnOdd;
    private JButton btnPlay;
    private JButton btnRed;
    private JButton btnSplit1;
    private JButton btnSplit10;
    private JButton btnSplit11;
    private JButton btnSplit12;
    private JButton btnSplit13;
    private JButton btnSplit14;
    private JButton btnSplit15;
    private JButton btnSplit16;
    private JButton btnSplit17;
    private JButton btnSplit18;
    private JButton btnSplit19;
    private JButton btnSplit2;
    private JButton btnSplit20;
    private JButton btnSplit21;
    private JButton btnSplit22;
    private JButton btnSplit23;
    private JButton btnSplit24;
    private JButton btnSplit25;
    private JButton btnSplit26;
    private JButton btnSplit27;
    private JButton btnSplit28;
    private JButton btnSplit29;
    private JButton btnSplit3;
    private JButton btnSplit30;
    private JButton btnSplit31;
    private JButton btnSplit32;
    private JButton btnSplit33;
    private JButton btnSplit34;
    private JButton btnSplit35;
    private JButton btnSplit36;
    private JButton btnSplit37;
    private JButton btnSplit38;
    private JButton btnSplit39;
    private JButton btnSplit4;
    private JButton btnSplit40;
    private JButton btnSplit41;
    private JButton btnSplit42;
    private JButton btnSplit43;
    private JButton btnSplit44;
    private JButton btnSplit45;
    private JButton btnSplit46;
    private JButton btnSplit47;
    private JButton btnSplit48;
    private JButton btnSplit49;
    private JButton btnSplit5;
    private JButton btnSplit50;
    private JButton btnSplit51;
    private JButton btnSplit52;
    private JButton btnSplit53;
    private JButton btnSplit54;
    private JButton btnSplit55;
    private JButton btnSplit56;
    private JButton btnSplit57;
    private JButton btnSplit6;
    private JButton btnSplit7;
    private JButton btnSplit8;
    private JButton btnSplit9;
    private JButton btnSquare1;
    private JButton btnSquare10;
    private JButton btnSquare11;
    private JButton btnSquare12;
    private JButton btnSquare13;
    private JButton btnSquare14;
    private JButton btnSquare15;
    private JButton btnSquare16;
    private JButton btnSquare17;
    private JButton btnSquare18;
    private JButton btnSquare19;
    private JButton btnSquare2;
    private JButton btnSquare20;
    private JButton btnSquare21;
    private JButton btnSquare22;
    private JButton btnSquare3;
    private JButton btnSquare4;
    private JButton btnSquare5;
    private JButton btnSquare6;
    private JButton btnSquare7;
    private JButton btnSquare8;
    private JButton btnSquare9;
    private JButton btnStreet1;
    private JButton btnStreet10;
    private JButton btnStreet11;
    private JButton btnStreet12;
    private JButton btnStreet2;
    private JButton btnStreet3;
    private JButton btnStreet4;
    private JButton btnStreet5;
    private JButton btnStreet6;
    private JButton btnStreet7;
    private JButton btnStreet8;
    private JButton btnStreet9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblBall;
    private javax.swing.JLabel lblGame;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblRoulette;
    private javax.swing.JLabel lblTable;
    private javax.swing.JPanel panelBets;
    private javax.swing.JPanel panelRoulette;
    private javax.swing.JTextField txtBet;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean hasEnoughMoney(double currentBalance) {
        return (currentBalance >= MIN_BET);
    }

    @Override
    public double resultInBalance(GameResult result, double betAmount) {
        return betAmount*result.getMultiplier();
    }
    
    //__________extra methods______________//
    
    private void ajustarBotonesApuestas() {
        // Coordenadas de los números (esquina superior izquierda de cada botón)
        int[] colX = {23, 62, 100, 138, 178, 217, 255, 293, 332, 372, 410, 448, 487}; // índice 0 = 0, 1..12 = columnas 1..36
        int[] rowY = {40, 97, 155}; // filas: superior, media, inferior

        // ---------- SPLITS (57) ----------
        java.util.ArrayList<int[]> splitPositions = new java.util.ArrayList<>();

        // Splits horizontales (entre columnas adyacentes, misma fila)
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 11; c++) { // c = 0..10 (entre columna c+1 y c+2)
                int x = (colX[c + 1] + colX[c + 2]) / 2;
                int y = rowY[f];
                splitPositions.add(new int[]{x, y});
            }
        }

        // Splits verticales (entre filas adyacentes, misma columna)
        for (int c = 0; c < 12; c++) {
            for (int f = 0; f < 2; f++) {
                int x = colX[c + 1];
                int y = (rowY[f] + rowY[f + 1]) / 2;
                splitPositions.add(new int[]{x, y});
            }
        }

        // Asignar posiciones a btnSplit1..btnSplit57
        for (int i = 0; i < 57 && i < splitPositions.size(); i++) {
            try {
                Component btn = (Component)this.getClass().getDeclaredField("btnSplit" + (i + 1)).get(this);
                if (btn != null) {
                    btn.setBounds(splitPositions.get(i)[0], splitPositions.get(i)[1], 20, 20);
                }
            } catch (Exception e) {
                // Ignorar si algún botón no existe
            }
        }

        // ---------- SQUARES (22) ----------
        java.util.ArrayList<int[]> squarePositions = new java.util.ArrayList<>();

        for (int f = 0; f < 2; f++) {
            for (int c = 0; c < 11; c++) {
                int x = (colX[c + 1] + colX[c + 2]) / 2;
                int y = (rowY[f] + rowY[f + 1]) / 2;
                squarePositions.add(new int[]{x, y});
            }
        }

        for (int i = 0; i < 22 && i < squarePositions.size(); i++) {
            try {
                Component btn = (Component)this.getClass().getDeclaredField("btnSquare" + (i + 1)).get(this);
                if (btn != null) {
                    btn.setBounds(squarePositions.get(i)[0], squarePositions.get(i)[1], 20, 20);
                }
            } catch (Exception e) {
                // Ignorar
            }
        }

        // ---------- LANES (six‑lines, 11) ----------
        java.util.ArrayList<int[]> lanePositions = new java.util.ArrayList<>();

        for (int c = 0; c < 11; c++) { // c = 0..10 (entre calle c+1 y c+2)
            int x = (colX[c + 1] + colX[c + 2]) / 2; // centrar el botón en la línea
            int y = 180; // fila fija debajo de los números
            lanePositions.add(new int[]{x, y});
        }

        for (int i = 0; i < 11 && i < lanePositions.size(); i++) {
            try {
                Component btn = (Component)this.getClass().getDeclaredField("btnLane" + (i + 1)).get(this);
                if (btn != null) {
                    btn.setBounds(lanePositions.get(i)[0], lanePositions.get(i)[1], 20, 20);
                }
            } catch (Exception e) {
                // Ignorar
            }
        }
    }
    
    private void inicializarBotonesApuesta() {
        // 1. Asignar nombres a todos los botones mediante reflexión
        // Números
        for (int i = 0; i <= 36; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("btn" + i);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }
        // Splits, Squares, Lanes (ya se asignan en ajustarBotonesApuesta)
        // Pero por si acaso, también puedes hacerlo aquí con bucles
        for (int i = 1; i <= 57; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("btnSplit" + i);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }
        for (int i = 1; i <= 22; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("btnSquare" + i);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }
        for (int i = 1; i <= 11; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("btnLane" + i);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }
        // Streets (no los ajustaste, pero también les asignamos nombre)
        for (int i = 1; i <= 12; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("btnStreet" + i);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }
        // Otros especiales (docenas, columnas, etc.)
        String[] specials = {"btn1st12", "btn2nd12", "btn3rd12", 
                             "btnColumn1", "btnColumn2", "btnColumn3",
                             "btn1st18", "btn2nd18", "btnEven", "btnOdd", 
                             "btnRed", "btnBlack"};
        for (String name : specials) {
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField(name);
                JButton btn = (JButton) field.get(this);
                btn.setName(field.getName());
            } catch (Exception e) { /* ignorar */ }
        }

        // 2. Ahora recorremos todos los botones para aplicar hover y selección
        Component[] components = jLayeredPane1.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;

                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setBorder(null);
                btn.setFocusPainted(false);

                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (btn != selectedButton) {
                            btn.setOpaque(true);
                            btn.setBackground(new Color(255, 255, 0, 100));
                            btn.setContentAreaFilled(true);
                            btn.repaint();
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (btn != selectedButton) {
                            btn.setOpaque(false);
                            btn.setContentAreaFilled(false);
                            btn.setBackground(null);
                            btn.repaint();
                        }
                    }
                });

                btn.addActionListener(e -> {
                    deseleccionarTodos();

                    // Pintar el botón seleccionado
                    btn.setOpaque(true);
                    btn.setBackground(new Color(255, 255, 0, 100));
                    btn.setContentAreaFilled(true);
                    btn.repaint();
                    selectedButton = btn;

                    // Actualizar typeHandler y rangeHandler
                    actualizarApuesta(btn);
                });
            }
        }
    }
    
    private void deseleccionarTodos() {
        for (Component comp : jLayeredPane1.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBackground(null);
                btn.repaint();
            }
        }
        selectedButton = null;
    }
    
    // Método para actualizar typeHandler y rangeHandler según el botón
    private void actualizarApuesta(JButton btn) {
        Arrays.fill(rangeHandler, -1);
        String nombre = btn.getName();
        if (nombre == null) return;

        if (nombre.startsWith("btnSplit")) {
            typeHandler = 2;
            int[] nums = obtenerNumerosCercanos(btn, 2);
            rangeHandler[0] = nums[0];
            rangeHandler[1] = nums[1];
        } else if (nombre.startsWith("btnStreet")) {
            typeHandler = 3;
            int[] nums = obtenerNumerosCercanos(btn, 3);
            for (int i = 0; i < 3; i++) rangeHandler[i] = nums[i];
        } else if (nombre.startsWith("btnSquare")) {
            typeHandler = 4;
            int[] nums = obtenerNumerosCercanos(btn, 4);
            for (int i = 0; i < 4; i++) rangeHandler[i] = nums[i];
        } else if (nombre.startsWith("btnLane")) {
            typeHandler = 5;
            int[] nums = obtenerNumerosCercanos(btn, 6);
            for (int i = 0; i < 6; i++) rangeHandler[i] = nums[i];
        } else if (nombre.matches("btn\\d+")) {
            // Pleno
            typeHandler = 1;
            int numero = Integer.parseInt(nombre.substring(3));
            rangeHandler[0] = numero;
        } else {
            // Apuestas especiales (docenas, columnas, etc.)
            switch (nombre) {
                case "btn1st12":
                    typeHandler = 6;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i + 1;
                    break;
                case "btn2nd12":
                    typeHandler = 6;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i + 13;
                    break;
                case "btn3rd12":
                    typeHandler = 6;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i + 25;
                    break;
                case "btnColumn1":
                    typeHandler = 7;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i * 3 + 1;
                    break;
                case "btnColumn2":
                    typeHandler = 7;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i * 3 + 2;
                    break;
                case "btnColumn3":
                    typeHandler = 7;
                    for (int i = 0; i < 12; i++) rangeHandler[i] = i * 3 + 3;
                    break;
                case "btn1st18":
                    typeHandler = 7;
                    for (int i = 0; i < 18; i++) rangeHandler[i] = i + 1;
                    break;
                case "btn2nd18":
                    typeHandler = 7;
                    for (int i = 0; i < 18; i++) rangeHandler[i] = i + 19;
                    break;
                case "btnEven":
                    typeHandler = 7;
                    for (int i = 0; i < 18; i++) rangeHandler[i] = (i + 1) * 2;
                    break;
                case "btnOdd":
                    typeHandler = 7;
                    for (int i = 0; i < 18; i++) rangeHandler[i] = i * 2 + 1;
                    break;
                case "btnRed":
                    typeHandler = 7;
                    System.arraycopy(rojos, 0, rangeHandler, 0, rojos.length);
                    break;
                case "btnBlack":
                    typeHandler = 7;
                    System.arraycopy(negros, 0, rangeHandler, 0, negros.length);
                    break;
                default:
                    typeHandler = 0;
                    break;
            }
        }

        lblInfo.setBackground(Color.white);
        lblInfo.setForeground(Color.black);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rangeHandler.length; i++) {
            if (rangeHandler[i] != -1) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(rangeHandler[i]);
            }
        }
        
        lblInfo.setText("Números seleccionados: "+sb.toString());

    }
    
    private JButton[] getBotonesNumeros() {
        if (botonesNumeros == null) {
            botonesNumeros = new JButton[37]; // índices 0..36
            for (int i = 0; i <= 36; i++) {
                try {
                    java.lang.reflect.Field field = this.getClass().getDeclaredField("btn" + i);
                    botonesNumeros[i] = (JButton) field.get(this);
                } catch (Exception e) {
                    botonesNumeros[i] = null;
                }
            }
        }
        return botonesNumeros;
    }

    private int[] obtenerNumerosCercanos(JButton btn, int k) {
        JButton[] nums = getBotonesNumeros();
        int cx = btn.getX() + btn.getWidth() / 2;
        int cy = btn.getY() + btn.getHeight() / 2;

        java.util.List<int[]> distancias = new java.util.ArrayList<>();
        for (int i = 1; i <= 36; i++) { // omitimos el 0 porque no se usa en estas apuestas
            if (nums[i] == null) continue;
            int nx = nums[i].getX() + nums[i].getWidth() / 2;
            int ny = nums[i].getY() + nums[i].getHeight() / 2;
            double d = Math.hypot(cx - nx, cy - ny);
            distancias.add(new int[]{i, (int) d});
        }
        // Ordenar por distancia (menor a mayor)
        distancias.sort((a, b) -> Integer.compare(a[1], b[1]));
        // Tomar los primeros k
        int[] result = new int[k];
        for (int i = 0; i < k && i < distancias.size(); i++) {
            result[i] = distancias.get(i)[0];
        }
        return result;
    }
    
    // Auxiliares para obtener números según posición
    private int numeroEnPosicion(int fila, int columna) {
        // fila: 0=superior (3,6,9...), 1=media (2,5,8...), 2=inferior (1,4,7...)
        // columna: 0..11
        int base = 0;
        switch (fila) {
            case 0: base = 3; break;
            case 1: base = 2; break;
            case 2: base = 1; break;
            default: base = 0;
        }
        return base + 3 * columna;
    }

    private int[] calcularNumerosSplit(int index) {
        // Los 33 primeros son horizontales (3 filas × 11 separaciones)
        if (index <= 33) {
            int fila = (index - 1) / 11;
            int columna = (index - 1) % 11;
            int num1 = numeroEnPosicion(fila, columna);
            int num2 = numeroEnPosicion(fila, columna + 1);
            return new int[]{num1, num2};
        } else {
            // Los 24 restantes son verticales (2 separaciones × 12 columnas)
            int idx = index - 34; // 0..23
            int separacion = idx / 12; // 0: entre fila0-fila1, 1: entre fila1-fila2
            int columna = idx % 12;
            int num1 = numeroEnPosicion(separacion, columna);
            int num2 = numeroEnPosicion(separacion + 1, columna);
            return new int[]{num1, num2};
        }
    }

    private int[] calcularNumerosSquare(int index) {
        // Los 11 primeros: separación fila0-fila1, columnas 0..10
        // Los 11 siguientes: separación fila1-fila2, columnas 0..10
        int separacionFila, columna;
        if (index <= 11) {
            separacionFila = 0;
            columna = index - 1;
        } else {
            separacionFila = 1;
            columna = index - 12;
        }
        int num1 = numeroEnPosicion(separacionFila, columna);
        int num2 = numeroEnPosicion(separacionFila, columna + 1);
        int num3 = numeroEnPosicion(separacionFila + 1, columna);
        int num4 = numeroEnPosicion(separacionFila + 1, columna + 1);
        return new int[]{num1, num2, num3, num4};
    }

    private int[] calcularNumerosLane(int index) {
        // Cubre dos columnas adyacentes (columna index-1 y index)
        int columna1 = index - 1;
        int columna2 = index;
        int[] nums = new int[6];
        int pos = 0;
        for (int fila = 0; fila < 3; fila++) {
            nums[pos++] = numeroEnPosicion(fila, columna1);
            nums[pos++] = numeroEnPosicion(fila, columna2);
        }
        return nums;
    }
}
