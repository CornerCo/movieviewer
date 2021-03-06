package com.github.cornerco.movieviewer;

import java.io.File;
import javax.swing.JDialog;


/*
 * SetupScreen.java
 *
 * Created on 2 March 2008, 08:13
 */
;

/**
 *
 * @author Peter Hoek
 */
public class ManageCategoriesDialog extends JDialog {

    private final MovieViewer parent;

    public ManageCategoriesDialog(MovieViewer parent) {
        super(parent, true);

        this.parent = parent;

        initComponents();

        refresh();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public final void refresh() {
        categoryList.setListData(MovieManager.getInstance().getCategories().toArray(new Category[0]));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("deprecation")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        categoryList = new javax.swing.JList<>();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        setBackgroundButton = new javax.swing.JButton();
        defaultButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Setup");
        setBackground(new java.awt.Color(204, 204, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(204, 204, 255), new java.awt.Color(0, 0, 204), new java.awt.Color(153, 153, 255)));

        categoryList.setBackground(new java.awt.Color(232, 250, 232));
        categoryList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Categories", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), new java.awt.Color(0, 153, 153))); // NOI18N
        categoryList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        categoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(categoryList);

        addButton.setBackground(new java.awt.Color(204, 255, 255));
        addButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        addButton.setLabel("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setBackground(new java.awt.Color(255, 204, 204));
        removeButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        removeButton.setLabel("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        editButton.setBackground(new java.awt.Color(255, 255, 204));
        editButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        saveButton.setBackground(new java.awt.Color(204, 255, 204));
        saveButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        setBackgroundButton.setBackground(new java.awt.Color(255, 255, 204));
        setBackgroundButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        setBackgroundButton.setText("background image");
        setBackgroundButton.setActionCommand("Attach background image");
        setBackgroundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setBackgroundButtonActionPerformed(evt);
            }
        });

        defaultButton.setBackground(new java.awt.Color(255, 255, 204));
        defaultButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        defaultButton.setText("Use as DEFAULT");
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(removeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(editButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(saveButton)
                            .addComponent(defaultButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                        .addGap(25, 25, 25))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(setBackgroundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultButton)
                        .addGap(23, 23, 23)
                        .addComponent(setBackgroundButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
                        .addComponent(saveButton)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        parent.refreshCategoryList();
        parent.refreshBackground();
        
        dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Category selected = categoryList.getSelectedValue();
        if (selected != null) {
            MovieManager.getInstance().removeCategory(selected);
        }

        refresh();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        Category selected = categoryList.getSelectedValue();
        if (selected != null) {
            MovieManager.getInstance().removeCategory(selected);
            new EditCategoryDialog(this, selected);
            refresh();
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        new EditCategoryDialog(this);
        refresh();
    }//GEN-LAST:event_addButtonActionPerformed

    private void setBackgroundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setBackgroundButtonActionPerformed
        Category category = categoryList.getSelectedValue();
        if (category != null) {

            File selected = new SelectFileDialog(this).getSelectedFile();
            if (selected != null) {
                category.setBackground(selected.getAbsolutePath());
            }
        }
    }//GEN-LAST:event_setBackgroundButtonActionPerformed

    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultButtonActionPerformed
        Category selected = categoryList.getSelectedValue();
        if (selected != null) {
            MovieManager.getInstance().setDefaultCategory(selected);
        }
    }//GEN-LAST:event_defaultButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList<Category> categoryList;
    private javax.swing.JButton defaultButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton setBackgroundButton;
    // End of variables declaration//GEN-END:variables
}
