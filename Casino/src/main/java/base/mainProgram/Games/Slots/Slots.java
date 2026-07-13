/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package base.mainProgram.Games.Slots;

import base.accountsHandler.User;
import base.mainProgram.Games.GameResult;
import base.mainProgram.Games.Playable;
import base.mainProgram.Home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

/**
 *
 * @author User
 */
public class Slots extends javax.swing.JFrame implements Playable {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Slots.class.getName());

    private static final int[] numRoll1 = {1,2,1,3,1,2,1,1,4,2,1,3,1,2,1,1,3,2,1,2};
    private static final int[] numRoll2 = {2,1,1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1};
    private static final int[] numRoll3 = {1,3,1,2,1,1,2,1,3,1,2,1,1,4,2,1,3,1,2,1};

    private final SimpleSlotRoll roll1 = new SimpleSlotRoll(numRoll1);
    private final SimpleSlotRoll roll2 = new SimpleSlotRoll(numRoll2);
    private final SimpleSlotRoll roll3 = new SimpleSlotRoll(numRoll3);

    private final User loggedUser;
    
    private static final double MIN_BET = 0.10;
    private static final String GAME_NAME = "Slots";
    
    RollPanel slotRoll1 = new RollPanel();
    RollPanel slotRoll2 = new RollPanel();
    RollPanel slotRoll3 = new RollPanel();
    
    private double result;
    private boolean updated = false;
    
    /**
     * Creates new form Slots
     * @param loggedUser
     */
    
    public Slots(User loggedUser) {
        initComponents();
        this.loggedUser = loggedUser;
        lblGame.setText(GAME_NAME);
        reloadData();
        setPanels();
        
        
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
    
    private GameResult play(double bet){
        int num1 = (int)Math.round(Math.random()*20);
        int num2 = (int)Math.round(Math.random()*20);
        int num3 = (int)Math.round(Math.random()*20);
        
        SymbolNode result1 = roll1.getNodes().get(num1);
        SymbolNode result2 = roll2.getNodes().get(num2);
        SymbolNode result3 = roll3.getNodes().get(num3);
        
        slotRoll1.pauseAt(result1.getSymbol());
        slotRoll2.pauseAt(result2.getSymbol());
        slotRoll3.pauseAt(result3.getSymbol());
        
        String resCompare1 = result1.getSymbol().getDescription();
        String resCompare2 = result2.getSymbol().getDescription();
        String resCompare3 = result3.getSymbol().getDescription();
        
        if(resCompare1.equals(resCompare2) && resCompare2.equals(resCompare3)){
            switch(result1.getType()){
                case 1: return new GameResult(true,3);
                case 2: return new GameResult(true,9);
                case 3: return new GameResult(true,24);
                case 4: return new GameResult(true,199);
            }
        }
        return new GameResult(false,-1);
    }
    
    private void setPanels(){
        
        slotRoll1.addRollPanelListener(new RollPanelListener() {
        @Override
        public void onRollPaused() {
            reactivateButton();
            updateBalance();
            updateInfoLabel();
        }
        });
        
        slotRoll2.addRollPanelListener(new RollPanelListener() {
        @Override
        public void onRollPaused() {
            reactivateButton();
            updateBalance();
            updateInfoLabel();
        }
        });
        
        slotRoll3.addRollPanelListener(new RollPanelListener() {
        @Override
        public void onRollPaused() {
            reactivateButton();
            updateBalance();
            updateInfoLabel();
        }
        });
        
        slot1.add(slotRoll1);
        for (SymbolNode node : roll1.getNodes()) {
            slotRoll1.addIcon(node.getSymbol());
        }
        slot2.add(slotRoll2);
        for (SymbolNode node : roll2.getNodes()) {
            slotRoll2.addIcon(node.getSymbol());
        }
        slot3.add(slotRoll3);
        for (SymbolNode node : roll3.getNodes()) {
            slotRoll3.addIcon(node.getSymbol());
        }
        
        
        slotRoll1.start();
        slotRoll2.start();
        slotRoll3.start();
    }
    
    private void reactivateButton(){
        if(slotRoll1.isPaused() && slotRoll2.isPaused() && slotRoll2.isPaused()) btnPlay.setEnabled(true);
    }
    
    private void updateBalance(){
        if(btnPlay.isEnabled() && !updated){
            loggedUser.setBalance(loggedUser.getBalance()+result);
            reloadBalance();
            updated = true;
        }
    }
    
    private void updateInfoLabel(){
        if(updated) return;
        if(result > 0){
            lblInfo.setForeground(Color.GREEN);
            lblInfo.setText("¡Felicidades, ganaste $"+result+"!");
        }else{
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

        jPanel1 = new JPanel();
        lblBalance = new javax.swing.JLabel();
        lblGame = new javax.swing.JLabel();
        slot1 = new JPanel();
        slot2 = new JPanel();
        slot3 = new JPanel();
        txtBet = new javax.swing.JTextField();
        btnPlay = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 530));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setMinimumSize(new java.awt.Dimension(500, 400));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblBalance.setText("Balance: $00.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        jPanel1.add(lblBalance, gridBagConstraints);

        lblGame.setText("Slots Game");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 75, 0);
        jPanel1.add(lblGame, gridBagConstraints);

        slot1.setBackground(new Color(0, 204, 51));
        slot1.setMinimumSize(new java.awt.Dimension(100, 250));
        slot1.setPreferredSize(new java.awt.Dimension(100, 250));
        slot1.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 20, 0);
        jPanel1.add(slot1, gridBagConstraints);

        slot2.setBackground(new Color(153, 255, 0));
        slot2.setMinimumSize(new java.awt.Dimension(100, 250));
        slot2.setPreferredSize(new java.awt.Dimension(100, 250));
        slot2.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(17, 0, 17, 0);
        jPanel1.add(slot2, gridBagConstraints);

        slot3.setBackground(new Color(51, 204, 0));
        slot3.setMinimumSize(new java.awt.Dimension(100, 250));
        slot3.setPreferredSize(new java.awt.Dimension(100, 250));
        slot3.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(17, 0, 17, 40);
        jPanel1.add(slot3, gridBagConstraints);

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

        lblInfo.setBackground(Color.darkGray);
        lblInfo.setFont(new Font("Segoe UI", 1, 18)); // NOI18N
        lblInfo.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 25, 0);
        jPanel1.add(lblInfo, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        
        double bet;
        try{
            bet = Double.parseDouble(txtBet.getText());
            
            if(bet >= MIN_BET){
                if(bet <= loggedUser.getBalance()){
                    slotRoll1.start();
                    slotRoll2.start();
                    slotRoll3.start();
        
                    result = resultInBalance(play(bet),bet);
                    updated = false;
                    
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.dispose();
        Home home = new Home(loggedUser);
        home.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

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
    private javax.swing.JButton btnPlay;
    private JPanel jPanel1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblGame;
    private javax.swing.JLabel lblInfo;
    private JPanel slot1;
    private JPanel slot2;
    private JPanel slot3;
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
}
