package com.github.cornerco.movieviewer;

/*
 * Created on 26 February 2008, 09:03
 * 
 */
/**
 *
 * @author hoekp
 */
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MovieViewer extends javax.swing.JFrame {

    private enum SortMethod {

        ALPHABETICAL("Alphabetical", new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                return a.getName().compareTo(b.getName());
            }
        }),
        VIEWINGS("   Viewings   ", new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                return b.getViews() - a.getViews();
            }
        });
        private final String buttonLabel;
        private final Comparator<Movie> comparator;

        private SortMethod(String buttonLabel, Comparator<Movie> comparator) {
            this.comparator = comparator;
            this.buttonLabel = buttonLabel;
        }

        public Comparator<Movie> getComparator() {
            return comparator;
        }

        public String getButtonLabel() {
            return buttonLabel;
        }
    }
    private SortMethod sortMethod = SortMethod.ALPHABETICAL;
    private boolean showCategoriesList = true;
    private Category activeCategory;

    public MovieViewer() {
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();

        movieList.setFocusTraversalKeysEnabled(false);
        movieList.setCellRenderer(new MovieCellRenderer());
        movieList.setSelectedIndex(0);

        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(bounds.width, bounds.height);
        setPreferredSize(new Dimension(bounds.width, bounds.height));
        setLocation(new Point(0, 0));
        getContentPane().setBackground(new Color(132, 176, 142));

        movieList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left-arrow");
        movieList.getActionMap().put("left-arrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int size = (movieList.getModel()).getSize() - 1;
                int currentIndexMinus1 = movieList.getSelectedIndex() - 1;
                if (currentIndexMinus1 < 0) {
                    movieList.setSelectedIndex(size);
                    movieList.ensureIndexIsVisible(size);
                } else {
                    movieList.setSelectedIndex(currentIndexMinus1);
                    movieList.ensureIndexIsVisible(currentIndexMinus1);
                }
            }
        });

        movieList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right-arrow");
        movieList.getActionMap().put("right-arrow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int idx = movieList.getSelectedIndex() + 1;

                if (idx > movieList.getLastVisibleIndex()) {
                    movieList.setSelectedIndex(0);
                } else {
                    movieList.setSelectedIndex(idx);
                }

                movieList.ensureIndexIsVisible(idx);
            }
        });
    }

    public void activateCategory(Category c) {
        activeCategory = c;

        if (c != null) {
            categoryList.setSelectedValue(c, true);

            refreshBackground();
        }

        refeshSortMethod();
        movieList.setSelectedIndex(0);
        refreshMetrics();
    }

    public void initialize() {
        refreshCategoryList();
        activateCategory(MovieManager.getInstance().getDefaultCategory());
    }

    public void refreshCategoryList() {
        Object selected = categoryList.getSelectedValue();
        ArrayList<Category> data = new ArrayList<>(MovieManager.getInstance().getCategories());
        Collections.sort(data, new Comparator<Category>() {
            @Override
            public int compare(Category a, Category b) {
                return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
            }
        });
        categoryList.setListData(data.toArray(new Category[0]));

        if (activeCategory == null || !MovieManager.getInstance().getCategories().contains(activeCategory)) {
            activeCategory = null;

            if (MovieManager.getInstance().getDefaultCategory() != null) {
                activateCategory(MovieManager.getInstance().getDefaultCategory());
            }
        } else {
            categoryList.setSelectedValue(selected, true);

            refreshMovieList();
        }

        refreshMetrics();
    }

    public void refreshBackground() {
        ImageJList i = (ImageJList) movieList;
        i.setImage(activeCategory.getBackground());
    }

    public void refreshMovieList() {
        if (activeCategory == null) {
            if (MovieManager.getInstance().getDefaultCategory() != null) {
                activateCategory(MovieManager.getInstance().getDefaultCategory());
            } else {
                movieList.setListData(new ArrayList<>().toArray(new Movie[0]));
            }
        } else {
            List<Movie> data = new ArrayList<>(activeCategory.getMovies());
            Collections.sort(data, sortMethod.getComparator());
            movieList.setListData(data.toArray(new Movie[0]));
        }
    }

    public void rotateSortMethod() {
        sortMethod = SortMethod.values()[(sortMethod.ordinal() + 1) % SortMethod.values().length];

        refeshSortMethod();
    }

    private void refeshSortMethod() {
        rotateSortMethodButton.setText(sortMethod.getButtonLabel());

        Object selection = movieList.getSelectedValue();
        refreshMovieList();
        movieList.setSelectedValue(selection, true);
    }

    public void refreshMetrics() {
        Movie m = movieList.getSelectedValue();
        if (m != null) {
            watchedSelectedLabel.setText(String.valueOf(m.getViews()));
        }

        numMoviesLabel.setText(String.valueOf(MovieManager.getInstance().getTotalMovies()));
        numMoviesViewedLabel.setText(String.valueOf(MovieManager.getInstance().getTotalMoviesViewed()));
    }

    private Category getSelectedCategory() {
        return (Category) categoryList.getSelectedValue();
    }

    private void toggleShowCategoriesList() {
        if (!showCategoriesList) {
            categoryScrollPane.setVisible(true);
            categoryList.setSize(100, 700);
            categoryList.requestFocus();
            showCategoriesList = true;
            jButton3.setText("HIDE");
        } else {
            categoryScrollPane.setVisible(false);
            movieList.requestFocus();
            showCategoriesList = false;
            jButton3.setText("SHOW");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        movieScrollPane = new javax.swing.JScrollPane();
        movieList = new ImageJList<>(Util.getMovieBackgroundPath())
        ;
        categoryScrollPane = new javax.swing.JScrollPane();
        categoryList = new ImageJList<>(Util.getCategoryBackgroundPath());
        jPanel2 = new javax.swing.JPanel();
        rotateSortMethodButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        numMoviesViewedLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        watchedSelectedLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        numMoviesLabel = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Hoek's Movie Viewer");
        setBackground(new java.awt.Color(0, 51, 153));
        setBounds(new java.awt.Rectangle(0, 0, 1024, 768));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(0, 51, 153));
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 1024, 768));
        setResizable(false);

        movieScrollPane.setBackground(new java.awt.Color(204, 255, 255));
        movieScrollPane.setForeground(new java.awt.Color(51, 255, 255));
        movieScrollPane.setAlignmentX(0.2F);
        movieScrollPane.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        movieList.setBackground(new java.awt.Color(0, 51, 153));
        movieList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MOVIES", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 3, 18), new java.awt.Color(156, 255, 255))); // NOI18N
        movieList.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        movieList.setForeground(new java.awt.Color(255, 204, 102));
        movieList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        movieList.setAutoscrolls(false);
        movieList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        movieList.setSelectedIndex(0);
        movieList.setVisibleRowCount(0);
        movieList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                movieListMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                movieListMouseClicked(evt);
            }
        });
        movieList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                movieListValueChanged(evt);
            }
        });
        movieList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                movieListKeyReleased(evt);
            }
        });
        movieScrollPane.setViewportView(movieList);

        categoryList.setBackground(new Color (220,246,220,0));
        categoryList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CATEGORIES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 3, 18), Color.black));
        categoryList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        categoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoryList.setAlignmentX(0.2F);
        categoryList.setSelectionForeground(new java.awt.Color(0, 0, 0));
        categoryList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoryListMouseClicked(evt);
            }
        });
        categoryList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                categoryListFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                categoryListFocusLost(evt);
            }
        });
        categoryList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categoryListKeyReleased(evt);
            }
        });
        categoryScrollPane.setViewportView(categoryList);

        jPanel2.setBackground(new java.awt.Color(132, 176, 142));

        rotateSortMethodButton.setBackground(new java.awt.Color(204, 255, 204));
        rotateSortMethodButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rotateSortMethodButton.setText("Alphabetical");
        rotateSortMethodButton.setFocusable(false);
        rotateSortMethodButton.setIconTextGap(1);
        rotateSortMethodButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateSortMethodButtonActionPerformed(evt);
            }
        });

        aboutButton.setBackground(new java.awt.Color(204, 255, 204));
        aboutButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        aboutButton.setText("About");
        aboutButton.setFocusable(false);
        aboutButton.setIconTextGap(1);
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Total viewed movies:");

        numMoviesViewedLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        numMoviesViewedLabel.setText("0");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Selected movie's views:");

        watchedSelectedLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        watchedSelectedLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        watchedSelectedLabel.setText("0");
        watchedSelectedLabel.setFocusable(false);
        watchedSelectedLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Total movies:");

        numMoviesLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        numMoviesLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        numMoviesLabel.setText("0");
        numMoviesLabel.setFocusable(false);
        numMoviesLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButton3.setBackground(new java.awt.Color(204, 255, 204));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/resources/zoomIcon.gif"))); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setSelected(true);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        exitButton.setBackground(new java.awt.Color(204, 255, 204));
        exitButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/resources/exitIcon.png"))); // NOI18N
        exitButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
        exitButton.setFocusable(false);
        exitButton.setSelected(true);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(204, 255, 204));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/resources/editIcon.gif"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setSelected(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(watchedSelectedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numMoviesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numMoviesViewedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(rotateSortMethodButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aboutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aboutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rotateSortMethodButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(watchedSelectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(numMoviesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(numMoviesViewedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(movieScrollPane)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(movieScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(categoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean checkAuthorised(Movie m, int x, int y) {
        if (m.isProtected()) {
            if (x > (((int) getSize().getWidth()) - 153)) {
                x = ((int) getSize().getWidth()) - 153;
            }
            if (y > (((int) getSize().getHeight()) - 140)) {
                y = ((int) getSize().getHeight()) - 140;
            }

            if (Util.isCorrectPassword(new PasswordDialog(this, x, y).getPassword())) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    private void showMovie(Movie m) {
        new GettingMovieFrame(this, false, m.getFile().getAbsolutePath());

        m.setViews(m.getViews() + 1);
        refreshMetrics();

        try {
            Desktop.getDesktop().open(m.getFile());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void movieListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieListMouseClicked
        Movie m = movieList.getSelectedValue();
        if (m == null) {
            return;
        }

        if (!(evt.getButton() == java.awt.event.MouseEvent.BUTTON3)) {
            if (evt.getClickCount() == 2) {
                Point location = movieList.indexToLocation(movieList.getSelectedIndex());
                if (checkAuthorised(m, location.x, location.y + 20)) {
                    showMovie(m);
                }
            }
        } else {
            Point location = movieList.indexToLocation(movieList.getSelectedIndex());
            if (checkAuthorised(m, location.x, location.y + 20)) {
                new RightClickContext(this, activeCategory, m, location.x, location.y + 20);
            }
        }
    }//GEN-LAST:event_movieListMouseClicked

    private void categoryListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_categoryListFocusGained
        categoryList.setSelectionBackground(Color.YELLOW);
    }//GEN-LAST:event_categoryListFocusGained

    private void categoryListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_categoryListFocusLost
        categoryList.setSelectionBackground(new Color(184, 207, 229, 150));
    }//GEN-LAST:event_categoryListFocusLost

    private void categoryListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryListMouseClicked
        activateCategory((Category) categoryList.getSelectedValue());
    }//GEN-LAST:event_categoryListMouseClicked

    private void categoryListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_X:
                if (evt.isControlDown()) {
                    dispose();
                }
                break;
            case KeyEvent.VK_Z:
                toggleShowCategoriesList();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                activateCategory(getSelectedCategory());
                break;
            case KeyEvent.VK_TAB:
                movieList.requestFocus();
                break;
        }
    }//GEN-LAST:event_categoryListKeyReleased

    private void movieListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieListMousePressed
        if ((evt.getButton() == java.awt.event.MouseEvent.BUTTON3)) {
            int location = movieList.locationToIndex(new Point(evt.getX(), evt.getY()));
            movieList.setSelectedIndex(location);
        }
    }//GEN-LAST:event_movieListMousePressed

    private void movieListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_movieListKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_X:
                if (evt.isControlDown()) {
                    MovieManager.getInstance().terminate();
                }
                break;
            case KeyEvent.VK_Z:
                toggleShowCategoriesList();
                break;
            case KeyEvent.VK_ENTER:
                Movie m = movieList.getSelectedValue();
                if (m != null) {
                    Point location = movieList.indexToLocation(movieList.getSelectedIndex());
                    if (checkAuthorised(m, location.x, location.y + 20)) {
                        showMovie(m);
                    }
                }
                break;
            case KeyEvent.VK_TAB:
                categoryList.requestFocus();
                break;
        }

        refreshMetrics();
    }//GEN-LAST:event_movieListKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SettingsFrame s = new SettingsFrame(this);
        s.setLocationRelativeTo(this);
        s.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        toggleShowCategoriesList();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void rotateSortMethodButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateSortMethodButtonActionPerformed
        rotateSortMethod();
    }//GEN-LAST:event_rotateSortMethodButtonActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        new AboutBox(this, true).setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        MovieManager.getInstance().terminate();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void movieListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_movieListValueChanged
        if (evt.getValueIsAdjusting()) {
            Movie m = movieList.getSelectedValue();
            if (m != null) {
                refreshMetrics();
            }
        }
    }//GEN-LAST:event_movieListValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JList<Category> categoryList;
    private javax.swing.JScrollPane categoryScrollPane;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JList<Movie> movieList;
    private javax.swing.JScrollPane movieScrollPane;
    private javax.swing.JLabel numMoviesLabel;
    private javax.swing.JLabel numMoviesViewedLabel;
    private javax.swing.JButton rotateSortMethodButton;
    private javax.swing.JLabel watchedSelectedLabel;
    // End of variables declaration//GEN-END:variables
}