/*
 * SelectFolder.java
 *
 * Created on 16 March 2008, 10:33
 */
package com.github.cornerco.movieviewer;

import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import javax.swing.JDialog;

/**
 *
 * @author Peter Hoek
 */
public class SelectFileDialog extends JDialog {

    private File selected;

    public SelectFileDialog(Window parent) {
        super(parent, "", Dialog.ModalityType.APPLICATION_MODAL);

        initComponents();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooser = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        chooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        chooser.setBackground(new java.awt.Color(204, 255, 204));
        chooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chooser, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooserActionPerformed
        if (evt.getActionCommand().equals("ApproveSelection")) {
            selected = chooser.getSelectedFile();       
        }

        dispose();
    }//GEN-LAST:event_chooserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser chooser;
    // End of variables declaration//GEN-END:variables

    public File getSelectedFile(){
        return selected;
    }
    
}
