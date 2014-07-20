package com.github.cornerco.movieviewer;

/*
 * Created on 26 February 2008, 09:03
 * 
 */
/**
 *
 * @author hoekp
 */
import java.util.*;
import java.io.*;

//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public final class MovieViewer extends javax.swing.JFrame {

    public static final String VERSION = "1.1.1";
    public static final String REVISION = "9";
    String[] categorisations = null;
    ArrayList currentFolders = new ArrayList();
    String password = "";
    String passwordmatch = "dad";
    Settings settings = new Settings();
    String startUpCategory = "";
    long last_refresh = 0;
    String search = "Alphabetical";
    PasswordJDialog1 pass = null;
    ArrayList<MovieEntry> movies = new ArrayList();
    boolean playing = false;
    String fullpathOfMoviePlaying = "";
    Boolean showcat = true;
    final String catNameFile = "CategoryNames.mvd";
    final String backgroundImagesFile = "BackgroundImage.mvd";
    final String catList = "CategoryProperties.mvd";
    final String catNameVariable = "categoryNames";
    final String defaultCat = "defaultCat";
    private boolean down;
    int xSize = 0;
    int ySize = 0;
    JPanel p = null;
    ImageIcon imageIcon2 = new ImageIcon("cat_background.gif");
    String startUpBackgroundImage = "";
    private static File f;
    private static FileChannel channel;
    private static FileLock lock;
    private File viewingProgram;

    public MovieViewer() {
        //   try {
        setUndecorated(true);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        // robot = new Robot();
        pass = new PasswordJDialog1(this, true);
        pass.setLocationRelativeTo(this);
        getDefaultProperties();
        startUpCategory = settings.getStartUpCategory();
        categorisations = settings.getAllCategories();

        System.out.println(startUpCategory);
        if (startUpCategory == null) {
            if (categorisations.length != 0) {
                startUpCategory = categorisations[0];

            } else {
                startUpCategory = defaultCat;
                categorisations[0] = defaultCat;
            }

        }

        // System.out.println("inside movieviewer startup category  " + startUpCategory);
        determineFilesinCategory(startUpCategory);

        //have to find default image before intialising
        startUpBackgroundImage = settings.getBackgroundImage(startUpCategory);
        if (startUpBackgroundImage == null) {
            startUpBackgroundImage = "defaultbackground.gif";
        }
        //System.out.println("inside movieviewer startup background image " + startUpBackgroundImage);
        initComponents();


        movieList.setFocusTraversalKeysEnabled(false);
        movieList.setFocusTraversalKeysEnabled(false);
        updateFileCount();
        movieList.setListData(new Vector(movies));
        categoryList.setSelectedValue(startUpCategory, true);
        movieList.setCellRenderer(new MovieCellRenderer());
        jLabel6.setText("" + settings.getTotalMoviesViewed());
        movieList.setSelectedIndex(0);
        Toolkit tk = Toolkit.getDefaultToolkit();

        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        xSize = ((int) tk.getScreenSize().getWidth());
        ySize = ((int) tk.getScreenSize().getHeight());

        setSize(bounds.getSize());
        setLocation(bounds.getLocation());

        playing = false;
        p = (JPanel) this.getContentPane();
        p.setBackground(new Color(132, 176, 142));

        /////////////////
        final String LEFT_ARROW = "left-arrow";
        final String RIGHT_ARROW = "right-arrow";
//         final String UP_ARROW = "up-arrow";
//        final String DOWN_ARROW = "down-arrow";
//
        movieList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), LEFT_ARROW);
        movieList.getActionMap().put(LEFT_ARROW, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                int size = (movieList.getModel()).getSize() - 1;
                System.out.println(size);
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


        movieList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), RIGHT_ARROW);
        movieList.getActionMap().put(RIGHT_ARROW, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {

                int currentIndexPlus1 = movieList.getSelectedIndex() + 1;

                if (currentIndexPlus1 > movieList.getLastVisibleIndex()) {
                    movieList.setSelectedIndex(0);
                } else {
                    movieList.setSelectedIndex(currentIndexPlus1);
                }
                movieList.ensureIndexIsVisible(currentIndexPlus1);

            }
        });

//        jList1.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0), UP_ARROW );
//        jList1.getActionMap().put( UP_ARROW, new AbstractAction()
//        {
//
//            public void actionPerformed(ActionEvent event)
//              {
//
//                    int currentIndex = jList1.getSelectedIndex();
//                    if(currentIndex+1>jList1.getLastVisibleIndex())
//                        jList1.setSelectedIndex(0);
//                    jList1.setSelectedIndex(currentIndex+1);
//
//
//
//              }
//            } );


    }

    public void removeMovieEntry(MovieEntry me) {
        movies.remove(me);
    }

    public void setCategorisations() {
        categorisations = settings.getAllCategories();
    }

    public void updateCategorisations() {
        categoryList.setListData(new Vector(Arrays.asList(categorisations)));
    }

    public void setStartUpCategory(String cat) {
        startUpCategory = cat;

    }

    public boolean passGood() {
        String s = password;
        password = "";
        if (s.equals(passwordmatch)) {
            return true;
        }
        return false;
    }

    public ArrayList<MovieEntry> getMovieList() {
        return movies;
    }

    private void getDefaultProperties() {
        //  if(!readSettings());
        //      setDefaultProperties();
        // readSettings();
        makeCategories();
        setCategorisations();
        getViewings();
        getProtectedMovies();
        readBackgroundImages();
    }

    private void setDefaultProperties() {
//         try{
//            settings.setStartUpCategory("Kids Movies");
//            ArrayList <File> kidsMovies = new ArrayList();
//            kidsMovies.add(new File("\\\\Blackbox\\movie disk 3\\MOVIES\\KIDS MOVIES"));
//            kidsMovies.add(new File("\\\\Blackbox\\Movie Disk\\Movies\\Kids Movies"));
//            kidsMovies.add(new File("H:\\KIDS MOVIES"));
//            kidsMovies.add(new File("F:\\Movies\\KIDS MOVIES"));
//            settings.addCategory("Kids Movies", kidsMovies);
//
//            //---------------------------------------------------------------------------------------------------------
//
//            ArrayList <File> MellissaMovies = new ArrayList();
//            MellissaMovies.add(new File("\\\\Blackbox\\movie disk 3\\MOVIES\\GENERAL MOVIES\\MELLISSA MOVIES"));
//            MellissaMovies.add(new File("\\\\Blackbox\\Movie Disk\\Movies\\General Movies\\Mellissa Movies"));
//            MellissaMovies.add(new File("e:\\GENERAL MOVIES\\MELLISSA MOVIES"));
//            MellissaMovies.add(new File("F:\\Movies\\GENERAL MOVIES\\MELLISSA MOVIES"));
//            settings.addCategory("Mellissa Movies", MellissaMovies);
//
//            //-------------------------------------------------------------------------------------------------------
//            ArrayList <File> Action = new ArrayList();
//            Action.add(new File("\\\\Blackbox\\movie disk 3\\MOVIES\\GENERAL MOVIES\\ACTION"));
//            Action.add(new File("\\\\Blackbox\\Movie Disk\\Movies\\General Movies\\Action"));
//            Action.add(new File("e:\\GENERAL MOVIES\\ACTION"));
//            Action.add(new File("F:\\Movies\\GENERAL MOVIES\\ACTION"));
//            settings.addCategory("Action", Action);
//
//            //-------------------------------------------------------------------
//
//            ArrayList <File> Comedy = new ArrayList();
//            Comedy.add(new File("\\\\Blackbox\\movie disk 3\\MOVIES\\GENERAL MOVIES\\COMEDY"));
//            Comedy.add(new File("\\\\Blackbox\\Movie Disk\\Movies\\General Movies\\comedy"));
//            Comedy.add(new File("e:\\GENERAL MOVIES\\COMEDY"));
//            Comedy.add(new File("F:\\Movies\\GENERAL MOVIES\\COMEDY"));
//            settings.addCategory("Comedy", Comedy);
//
//            //-------------------------------------------------------------------------
//
//            ArrayList <File> Other = new ArrayList();
//            Other.add(new File("e:\\GENERAL MOVIES\\OTHER MOVIES"));
//            Other.add(new File("\\\\Blackbox\\movie disk 3\\MOVIES\\GENERAL MOVIES\\OTHER MOVIES"));
//            Other.add(new File("\\\\Blackbox\\Movie Disk\\Movies\\General Movies\\other"));
//            Other.add(new File("F:\\Movies\\GENERAL MOVIES\\Other"));
//
//            settings.addCategory("Other", Other);
//
//            //-----------------------------------------------------------------------------
//            ArrayList <File> TV = new ArrayList();
//            TV.add(new File("e:\\tv files\\recording"));
//            TV.add(new File("\\\\blackbox\\TV FILES\\RECORDING"));
//            settings.addCategory("TV", TV);
//
//           //---------------------------------------------------------------------------------
//            ArrayList <File> HomeMovies = new ArrayList();
//            HomeMovies.add(new File("\\\\Blackbox\\Everio_Movies_photos"));
//            settings.addCategory("Home Movies", HomeMovies);
//
//            //writeSettings();
//
//        }catch(Exception e){e.printStackTrace();}
    }

    public void writeSettings() {
//        try {
//            // Use a FileOutputStream to send data to a file
//            // called myobject.data.
//            FileOutputStream f_out = new FileOutputStream("settings3.data");
//            // Use an ObjectOutputStream to send object data to the
//            // FileOutputStream for writing to disk.
//            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
//            // Pass our object to the ObjectOutputStream's
//            // writeObject() method to cause it to be written out
//            // to disk.
//            obj_out.writeObject(settings);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        this.saveCategoryNames();
        this.saveCategories();
        this.saveProtectedMovies();
        this.saveNumberofViewings();
        this.saveBackgroundFileloction();
    }

//    private boolean readSettings()
//    {
//        try {
//            // Read from disk using FileInputStream.
//            FileInputStream f_in = new FileInputStream("settings3.data");
//            //if(f_in==null)
//            //    return false;
//            // Read object using ObjectInputStream.
//            ObjectInputStream obj_in = new ObjectInputStream(f_in);
//            // Read an object.
//            Object obj = obj_in.readObject();
//            // Is the object that you read in, say, an instance
//
//            if (obj instanceof Settings) {
//                settings = (Settings) obj;
//            }
//        } catch (IOException ex) {
//            //Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//
//            return false;
//        } catch (ClassNotFoundException ex) {
//            //Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//
//      return true;
//    }
    private void determineFilesinCategory(String cat) {
        //long currentTime = System.currentTimeMillis();
        //if((currentTime - last_refresh) > 60000)
        //System.out.println("refreshing");
        // last_refresh = currentTime;
        // this.cat = cat;
        currentFolders = settings.getCategory(cat);
        if (currentFolders != null) {
            createMovieEntries(currentFolders);
        }

    }

    //for servlet
    /*   public void moveDown()
     {
     if(!this.isVisible())
     this.setVisible(true);
    
     int max = movies.size();
    
     int i = jList1.getSelectedIndex();
     int move = ++i;
     // System.out.println(i);
     if(move < max)
     jList1.setSelectedIndex(move);
    
     updateFileCount();
    
     }*/
    public void selectMovie(int index) {
        if (!this.isVisible()) {
            this.setVisible(true);
        }
        movieList.requestFocus();

        movieList.setSelectedIndex(index);
    }

    public String getDefaultCat() {
        return this.startUpCategory;
    }

    public int getSelectedCat() {
        return categoryList.getSelectedIndex();

    }

    /*   public void refreshScreen()
     {
     int catpointer = jList2.getSelectedIndex();
     int moviepointer = jList1.getSelectedIndex();
     if(catpointer == -1)
     catpointer = 0;
     if(moviepointer == -1)
     moviepointer = 0;
    
     jList2.setSelectedIndex(catpointer);
     jList1.setSelectedIndex(moviepointer);
    
    
     }
     * */
    /*    public void moveAcross()
     {
    
    
     if(!this.isVisible())
     this.setVisible(true);
    
     int max = movies.size();
    
     int i = jList1.getSelectedIndex();
     int step =(i+5);
     //System.out.println(step);
     if(step < max)
     jList1.setSelectedIndex(step);
    
     updateFileCount();
    
     }*/
    //for servlet
    public boolean isAlphaSet() {

        if (jButton2.getText().equals("Alphabetical")) {
            return true;
        }
        return false;
    }

    public void toggleAlpha() {

        //int selection = jList1.getSelectedIndex();
        if (jButton2.getText().equals("Alphabetical")) {
            jButton2.setText("   Viewings   ");
            this.setSearch("Alphabetical");
        } else {
            jButton2.setText("Alphabetical");
            this.setSearch("Viewings");
        }
        //refreshScreen();

        // System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        //createMovieEntries(currentFolders);
        // jList1.setListData(new Vector(movies);
        // jList1.setSelectedIndex(selection);
    }

    public void play() {
        if (!playing) {
            try {
                //this.setVisible(false);
                movieList.requestFocus();
                int selection = movieList.getSelectedIndex();
                MovieEntry me = movies.get(selection);
                settings.updateViewings(me.getFile().getAbsolutePath());
                createMovieEntries(currentFolders);
                // jList1.clearSelection();

                // jList1.setValueIsAdjusting(true);
                // jList1.setListData(new Vector(movies);
                //  jList1.setValueIsAdjusting(false);
                movieList.requestFocus();
                movieList.setSelectedIndex(selection);
                me = movies.get(selection);
                jLabel3.setText("" + me.getViewings());
                //writeSettings();

                showMovie(me.getFile().getAbsolutePath());


            } catch (NullPointerException ex) {
            }
        }
//       else
//       {
//           robot.keyPress(KeyEvent.VK_ENTER);
//             robot.keyRelease(KeyEvent.VK_ENTER);
//       }


    }

    public boolean isMovieDVD() {
        int selection = movieList.getSelectedIndex();
        MovieEntry me = movies.get(selection);

        if (fullpathOfMoviePlaying.endsWith("ifo") || fullpathOfMoviePlaying.endsWith("IFO")) {
            return true;
        }

        return false;

    }

    public String getPassword() {

        return passwordmatch;

    }

    public String getMoviePath() {
        int selection = movieList.getSelectedIndex();
        MovieEntry me = movies.get(selection);
        return me.getFile().getAbsolutePath();

    }

    public void nextChapter() {
//        if(isMovieDVD())
//        {
//           robot.keyPress(KeyEvent.VK_N); //next chaper
//           robot.keyRelease(KeyEvent.VK_N);
//
//        }
    }

    public void menuDown() {
//
//              robot.keyPress(KeyEvent.VK_DOWN); // press down in case of menu
//              robot.keyRelease(KeyEvent.VK_DOWN); // press down in case of menu
    }

    public void info() {
//             robot.keyPress(KeyEvent.VK_D); // press down in case of menu
//             robot.keyRelease(KeyEvent.VK_D);
    }

    public void jumpF() {
//             robot.keyPress(KeyEvent.VK_PAGE_DOWN); // press down in case of menu
//             robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
    }

    public void jumpB() {
//             robot.keyPress(KeyEvent.VK_PAGE_UP); // press down in case of menu
//             robot.keyRelease(KeyEvent.VK_PAGE_UP);
    }

    public void menuUp() {
//            robot.keyPress(KeyEvent.VK_UP); // press up in case of menu
//             robot.keyRelease(KeyEvent.VK_UP);
    }

    public void pevChapter() {
//        if(isMovieDVD())
//        {
//           robot.keyPress(KeyEvent.VK_P);
//           robot.keyRelease(KeyEvent.VK_P);
//
//        }
    }

    public void moveToPosition(int pos) {
//         robot.mouseMove(0,766);
//         robot.mouseMove(100,766);
//
//         int posx =  (563*pos/100) +243;
//         robot.mouseMove(posx, 757);
//         robot.mousePress(InputEvent.BUTTON1_MASK);
//         robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void pause() {
//       robot.keyPress(32);
    }

    public void closeApp() {
//      playing =false;
//      robot.keyPress(KeyEvent.VK_ALT);
//      robot.keyPress(KeyEvent.VK_F4);
//      robot.keyRelease(KeyEvent.VK_ALT);
//      robot.keyRelease(KeyEvent.VK_F4);
//      this.setVisible(true);
    }

    public String[] getCategorisations() {
        return categorisations;
    }

    public void setCategory(String cat) {
        categoryList.requestFocus();
        categoryList.setSelectedValue(cat, true);

        //System.out.println(cat);
        determineFilesinCategory(cat);
        updateFileCount();

        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(0);
    }

    public void createMovieEntries(ArrayList fileSet) {
        ArrayList mev = new ArrayList();
        ArrayList files = new ArrayList();
        for (int c = 0; c < fileSet.size(); c++) {

            getMovieFiles(files, (File) fileSet.get(c));
            // System.out.println(files);
            for (int c2 = 0; c2 < files.size(); c2++) {
                File f = (File) files.get(c2);
                MovieEntry me = new MovieEntry(f, search);
                // temp here to change all 0 or null viewing values to 1
                //if(settings.getViewings(me.getFile().getAbsolutePath())==0)

                //    settings.changeViewings(f.getAbsolutePath(),1);

                me.setViewings(settings.getViewings(me.getFile().getAbsolutePath()));


                if (settings.checkProtected(f.getAbsolutePath())) {
                    me.setProtected(true);
                }
                mev.add(me);

            }
            files.clear();
        }

        Collections.sort(mev);
        if (jLabel6 != null)//no start it hasn't be instantiated
        {
            jLabel6.setText("" + settings.getTotalMoviesViewed());
        }
        movies = mev;
    }

    private void getMovieFiles(ArrayList list, File dir) {
        File files[] = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getMovieFiles(list, file);
                } else {
                    String name = file.getName().toLowerCase();

                    if (name.equals("video_ts.ifo") || name.endsWith("wmv") || name.endsWith("avi")
                            || name.endsWith("mpg") || name.endsWith("mpeg") || name.endsWith("mod")
                            || name.endsWith("ts") || name.endsWith("m2ts") || name.endsWith("mkv")
                            || name.endsWith("mp4")) {
                        list.add(file);
                    }
                }
            }
        }
    }

    public void addFileToProtected() {
        int index = movieList.getSelectedIndex();

        MovieEntry me = (MovieEntry) movies.get(index);

        settings.addProctedMovie(me.getFile());
        createMovieEntries(currentFolders);
        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(index);
    }

    public void refreshFileList(boolean DeleteFlag) {
        int index = movieList.getSelectedIndex();
        createMovieEntries(currentFolders);
        movieList.setListData(new Vector(movies));
        if (DeleteFlag == true) {
            if (index == 0) {
                index = 1;
            } else {
                index = index - 1;
            }
        }
        movieList.setSelectedIndex(index);
    }

    public void removeFileFromProtected() {
        int index = movieList.getSelectedIndex();
        MovieEntry me = (MovieEntry) movies.get(index);
        settings.removeProctedMovie(me.getFile());
        createMovieEntries(currentFolders);
        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(index);
    }

    public void setViewCounter(String moviepath, int count) {
        int index = movieList.getSelectedIndex();
        settings.changeViewings(moviepath, count);
        createMovieEntries(currentFolders);
        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(index);
    }

    private void updateFileCount() {
        movieCountLabel.setText(Integer.toString(movies.size()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        movieList = new ImageJList(startUpBackgroundImage);
        //{
            //    Image image = imageIcon.getImage();
            // Image grayImage = GrayFilter.createDisabledImage(image); comented line

            //    {setOpaque(false);} // instance initializer

            //    public void paintComponent (Graphics g) {
                //    g.drawImage(image, 0, 0, this);//chage draw to image
                //    super.paintComponent(g);
                //    }
            //};
        jScrollPane2 = new javax.swing.JScrollPane();
        categoryList = new javax.swing.JList()
        {
            Image image = imageIcon2.getImage();
            // Image grayImage = GrayFilter.createDisabledImage(image); comented line

            {setOpaque(false);} // instance initializer

            public void paintComponent (Graphics g) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(),this);//chage draw to image
                super.paintComponent(g);
            }
        };
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        movieCountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Hoek's Movie Viewer");
        setBackground(new java.awt.Color(0, 51, 153));
        setBounds(new java.awt.Rectangle(0, 0, 1024, 768));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(0, 51, 153));
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 1024, 768));
        setResizable(false);

        jScrollPane1.setBackground(new java.awt.Color(204, 255, 255));
        jScrollPane1.setForeground(new java.awt.Color(51, 255, 255));
        jScrollPane1.setAlignmentX(0.2F);
        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                movieListMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                movieListMousePressed(evt);
            }
        });
        movieList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                movieListKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                movieListKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                movieListKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(movieList);

        categoryList.setBackground(new Color (220,246,220,0));
        categoryList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CATEGORIES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 3, 18), Color.black));
        categoryList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        categoryList.setModel(new javax.swing.AbstractListModel() {
            //String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return categorisations.length; }
            public Object getElementAt(int i) { return categorisations[i]; }
        });
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
            public void keyTyped(java.awt.event.KeyEvent evt) {
                categoryListKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                categoryListKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categoryListKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(categoryList);

        jPanel1.setBackground(new java.awt.Color(132, 176, 142));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 20));

        jButton3.setBackground(new java.awt.Color(204, 255, 204));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/zoom-in-icon.gif"))); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setSelected(true);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(204, 255, 204));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/applications-icon.gif"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setSelected(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Movies");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("All Movies Viewed");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("0");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Watched selected");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("0");
        jLabel3.setFocusable(false);
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButton2.setBackground(new java.awt.Color(204, 255, 204));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setIconTextGap(1);
        jButton2.setLabel("Viewings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(204, 255, 204));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/exit_2.png"))); // NOI18N
        jButton4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
        jButton4.setFocusable(false);
        jButton4.setSelected(true);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 255, 204));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton5.setText("About");
        jButton5.setFocusable(false);
        jButton5.setIconTextGap(1);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        movieCountLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        movieCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        movieCountLabel.setText("0");
        movieCountLabel.setFocusable(false);
        movieCountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(movieCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(movieCountLabel))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel2.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public boolean checkProtected(String path) {
        if (settings.checkProtected(path)) {
            return true;
        }

        return false;

    }

    private boolean checkProtectedMovie(File movie, int x, int y) {
        if (checkProtected(movie.getAbsolutePath())) {
            if (x > (((int) getSize().getWidth()) - 153)) {
                x = ((int) getSize().getWidth()) - 153;
            }
            if (y > (((int) getSize().getHeight()) - 140)) {
                y = ((int) getSize().getHeight()) - 140;
            }

            pass.setVisible(x, y);
            if (passGood()) {
                return true;
            }
        } else {
            return true;
        }
        return false;

    }

    public void setPassword(String s) {
        password = s;
    }

    private void showMovie(String path) {

        /* int selection = jList1.getSelectedIndex();
         settings.updateViewings(path);
         writeSettings();
         createMovieEntries(currentFolders);
         if(movies==null)
         System.out.println("movies is null");
         if(jList1==null)
         System.out.println("jList1 is null");
        
         for(int c= 0; c< movies.size(); c++)
         {
         if(movies.get(c)==null)
         System.out.println("jList1 is null");
        
         }
        
         jList1.setListData(new Vector(movies);
         jList1.setSelectedIndex(selection);//make sure to set the selection again
         MovieEntry me = movies.get(selection);
         jLabel3.setText(""+me.getViewings());*/
        new GettingMovie(this, false, path);

        try {
            Runtime rt = Runtime.getRuntime();
            //System.out.println( "\"" +movie.getAbsolutePath()+"\"");
            //Process p = rt.exec("C:\\Program Files\\CyberLink\\PowerDVD\\PowerDVD.exe \"" + path+"\"" ) ;
            //doesn't work
            // Process p = rt.exec("C:\\test.bat \""+path+"\"") ;
            //Process p = rt.exec("C:\\test2.bat \""+path+"\"") ; // testing running the default appplication works with wmp
            // System.out.println("\""+path+"\"");
            Process p = rt.exec("cmd /c \"" + path + "\""); // this works for the default player application but can not set abovenormal
            //  Process p = rt.exec("cmd /c start /abovenormal C:\\\"Program Files\"\\CyberLink\\PowerDVD\\PowerDVD.exe"+" \""+path+"\""); //this works but the one below doesn't
            //  Process p = rt.exec("cmd /c start /abovenormal \"C:\\Program Files\\CyberLink\\PowerDVD\\PowerDVD.exe\" "+" \""+path+"\"");
            //  System.out.println("cmd /c start /abovenormal \""+viewingProgram.toString()+"\" \""+path+"\"");
            /////////////  Process p = rt.exec("cmd /c start /abovenormal "+viewingProgram.toString()+" \""+path+"\"");
            playing = true;
            fullpathOfMoviePlaying = path;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void movieListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieListMouseClicked

        if (!(evt.getButton() == java.awt.event.MouseEvent.BUTTON3)) {
            int selection = movieList.getSelectedIndex();
            MovieEntry me = movies.get(selection);
            File movie = me.getFile();
            jLabel3.setText("" + me.getViewings());

            if (evt.getClickCount() == 2) // Double-click
            {
                boolean check = checkProtectedMovie(movie, evt.getX(), evt.getY() + 30);
                if (check) {
                    settings.updateViewings(movie.getAbsolutePath());
                    //writeSettings();
                    createMovieEntries(currentFolders);
                    movieList.setListData(new Vector(movies));
                    me = movies.get(selection);
                    jLabel3.setText("" + me.getViewings());
                    showMovie(movie.getAbsolutePath());
                }
            }
        } else {
            int selection = movieList.getSelectedIndex();
            MovieEntry me = movies.get(selection);
            jLabel3.setText("" + me.getViewings());
            File movie = me.getFile();
            boolean check = checkProtectedMovie(movie, evt.getX(), evt.getY() + 20);

            if (check) {
                RightClickContext rcc = new RightClickContext(this, true, me);
                int xpos = evt.getX();
                int ypos = evt.getY() + 20;

                if (xpos > (((int) getSize().getWidth()) - 153)) {
                    xpos = ((int) getSize().getWidth()) - 153;
                }

                if (ypos > (((int) getSize().getHeight()) - 140)) {
                    ypos = ((int) getSize().getHeight()) - 140;
                }

                rcc.setLocation(xpos, ypos);
                rcc.setCheck(checkProtected(movie.getAbsolutePath()));
                rcc.setVisible(true);
            }

        }
    }//GEN-LAST:event_movieListMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SetupScreen1 s = new SetupScreen1(this);
        s.setLocationRelativeTo(this);
        s.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    @SuppressWarnings("static-access")
    private void movieListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_movieListKeyTyped
        if (evt.isControlDown()) {
            down = true;
        }
        if (evt.getKeyChar() == evt.VK_ENTER) {
            int selection = movieList.getSelectedIndex();
            MovieEntry me = movies.get(selection);
            File movie = me.getFile();
            Point location = movieList.indexToLocation(selection);
            boolean check = checkProtectedMovie(movie, location.x, location.y + 20);
            if (check) {

                settings.updateViewings(movie.getAbsolutePath());
                //writeSettings();
                createMovieEntries(currentFolders);
                movieList.setListData(new Vector(movies));
                movieList.setSelectedIndex(selection);
                me = movies.get(selection);
                jLabel3.setText("" + me.getViewings());
                showMovie(movie.getAbsolutePath());
            }
        }
    }//GEN-LAST:event_movieListKeyTyped

    private void categoryListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_categoryListFocusGained
        categoryList.setSelectionBackground(Color.YELLOW);
    }//GEN-LAST:event_categoryListFocusGained

    private void categoryListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_categoryListFocusLost
        categoryList.setSelectionBackground(new Color(184, 207, 229, 150));
    }//GEN-LAST:event_categoryListFocusLost

    private void categoryListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryListMouseClicked

        //int index = jList2.getSelectedIndex();
        String imageLocation = settings.getBackgroundImage((String) categoryList.getSelectedValue());
        determineFilesinCategory((String) categoryList.getSelectedValue());
        updateFileCount();

        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(0);
        ImageJList ijl = (ImageJList) movieList;
        if (imageLocation != null)//no image set
        {
            ijl.setImageIcon(imageLocation);
        } else {
            ijl.setImageIcon("defaultbackground.gif");
        }


    }//GEN-LAST:event_categoryListMouseClicked

    private void categoryListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyReleased
        int code = evt.getKeyCode();
        if (down && code == 88) {
            this.dispose();
        } else {
            down = false;
        }

        if (code == 90) {
            showHide();
        }




        if (code == java.awt.event.KeyEvent.VK_DOWN || code == java.awt.event.KeyEvent.VK_UP) {
            determineFilesinCategory((String) categoryList.getSelectedValue());
            updateFileCount();
            movieList.setListData(new Vector(movies));
            movieList.setSelectedIndex(0);

            String imageLocation = settings.getBackgroundImage((String) categoryList.getSelectedValue());
            ImageJList ijl = (ImageJList) movieList;
            if (imageLocation != null)//no image set
            {
                ijl.setImageIcon(imageLocation);
            } else {
                ijl.setImageIcon("defaultbackground.gif");
            }
        }
    }//GEN-LAST:event_categoryListKeyReleased

    private void movieListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieListMousePressed
        if ((evt.getButton() == java.awt.event.MouseEvent.BUTTON3)) {
            int location = movieList.locationToIndex(new Point(evt.getX(), evt.getY()));
            movieList.setSelectedIndex(location);
        }
    }//GEN-LAST:event_movieListMousePressed

    private void movieListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_movieListKeyReleased
        int code = evt.getKeyCode();

        if (down && code == 88) {
            this.dispose();
        } else {
            down = false;
        }

        if (code == 90) {
            showHide();
        }

        int selection = movieList.getSelectedIndex();
        if (!movies.isEmpty()) {
            MovieEntry me = movies.get(selection);
            jLabel3.setText("" + me.getViewings());
        }


    }//GEN-LAST:event_movieListKeyReleased

    private void setSearch(String search) {
        this.search = search;

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int selection = movieList.getSelectedIndex();
        toggleAlpha();
        createMovieEntries(currentFolders);
        movieList.setListData(new Vector(movies));
        movieList.setSelectedIndex(selection);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        showHide();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void categoryListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyTyped
        if (evt.isControlDown()) {
            down = true;
        }
    }//GEN-LAST:event_categoryListKeyTyped

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        writeSettings();

        System.exit(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void categoryListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
            movieList.requestFocus();
        }

    }//GEN-LAST:event_categoryListKeyPressed

    private void movieListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_movieListKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
            categoryList.requestFocus();
        }
    }//GEN-LAST:event_movieListKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        new AboutBox(this, true).setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    public static void main(String args[]) {
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
//        }

        try {
            f = new File("movieviewer.lock");
            if (f.exists()) {
                f.delete();
            }

            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                throw new RuntimeException("Only 1 instance of MovieViewer can run.");
            }

            final MovieViewer viewer = new MovieViewer();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    viewer.writeSettings();

                    if (lock != null) {
                        try {
                            lock.release();
                            channel.close();
                        } catch (IOException ex) {
                            Logger.getLogger(MovieViewer.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        f.delete();
                    }
                }
            });

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    SplashFrame splash = new SplashFrame();
                    splash.setVisible(true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            viewer.loadMovies();
                            viewer.setVisible(true);
                        }
                    }).start();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Could not start process.", e);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList categoryList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel movieCountLabel;
    private javax.swing.JList movieList;
    // End of variables declaration//GEN-END:variables

    private void getViewings() {
        Properties properties = new Properties();
        FileInputStream fin = null;
        try {
            FileInputStream catInput = new FileInputStream("NumberOfViewings.mvd");
            properties.load(catInput);
            catInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Enumeration e = properties.keys();

        while (e.hasMoreElements()) {
            //  settings.addProctedMovie(new File(properties.getProperty((String)e.nextElement())));
            String key = (String) e.nextElement();

            if (!(new File(key).exists())) {
                continue;
            }

            settings.changeViewings(key, Integer.parseInt(properties.getProperty(key)));
        }
    }

    private void getProtectedMovies() {
        Properties properties = new Properties();
        try {
            FileInputStream catInput = new FileInputStream("ProtectedMovies.mvd");
            properties.load(catInput);
            catInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String protectedList = properties.getProperty("protected");

        StringTokenizer st = new StringTokenizer(protectedList, ",");

        while (st.hasMoreTokens()) {

            settings.addProctedMovie(new File(st.nextToken()));

        }

    }

    private void saveCategoryNames() {
        Set<String> e = settings.getCategoriesEnumerator();
        Properties CategoryNames = new Properties();
        String categoryList = "";
        boolean first = true;

        Iterator<String> i = e.iterator();

        while (i.hasNext()) {
            String key = i.next();
            //System.out.println(key);
            if (first) {
                categoryList = key;
            } else {
                categoryList += "," + key;
            }
            first = false;
        }

        CategoryNames.setProperty(catNameVariable, categoryList);
        CategoryNames.setProperty(defaultCat, startUpCategory);

        try {
            FileOutputStream out = new FileOutputStream(catNameFile);
            CategoryNames.store(out, "---No Comment---");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void makeCategories() {
        Properties properties = new Properties();
        FileInputStream fin = null;
        try {
            FileInputStream catInput = new FileInputStream(catNameFile);
            properties.load(catInput);
            catInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String catNamesList = properties.getProperty(catNameVariable);
        StringTokenizer t = new StringTokenizer(catNamesList, ",");


        try {
            fin = new FileInputStream(catList);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        while (t.hasMoreTokens()) {
            String cat = t.nextToken();
            // System.out.println(cat);

            try {
                properties.load(fin);
                String directoryName = properties.getProperty(cat);
                StringTokenizer t2 = new StringTokenizer(directoryName, ",");
                ArrayList<File> list = new ArrayList();

                while (t2.hasMoreTokens()) {
                    list.add(new File(t2.nextToken()));
                }
                settings.addCategory(cat, list);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        try {
            fin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //System.out.println(properties.getProperty(defaultCat));
        settings.setStartUpCategory(properties.getProperty(defaultCat));
        //this.setCategorisations();

    }

    private void readBackgroundImages() {
        Properties properties = new Properties();

        try {
            FileInputStream backgroundInput = new FileInputStream(backgroundImagesFile);
            properties.load(backgroundInput);
            backgroundInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] cats = settings.getAllCategories();

        for (int c = 0; c < cats.length; c++) {
            String filename = properties.getProperty(cats[c]);
            //System.out.println(filename);
            if (filename != null) {
                settings.addCategoryImageLocation(cats[c], filename);
            }
        }
    }

    private void saveCategories() {

        Set<String> e = settings.getCategoriesEnumerator();
        Properties CategoryProperties = new Properties();

        Iterator<String> i = e.iterator();

        while (i.hasNext()) {
            String key = i.next();
            //System.out.println(key);
            String list = "";

            ArrayList<File> v = settings.getCategory(key);
            for (int c = 0; c < v.size(); c++) {
                //System.out.println("        "+ v.get(c).toString());
                if (c == 0) {
                    list = v.get(c).toString();
                } else {
                    list += "," + v.get(c).toString();
                }
            }
            CategoryProperties.setProperty(key, list);
        }
        try {
            FileOutputStream out = new FileOutputStream("CategoryProperties.mvd");
            CategoryProperties.store(out, "---No Comment---");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveProtectedMovies() {

        HashMap h = settings.getprotectedMovies();
        Set<String> e = h.keySet();
        Properties protectedProperties = new Properties();
        String list = "";
        boolean first = true;

        Iterator<String> i = e.iterator();

        while (i.hasNext()) {
            if (first) {
                list = i.next();
            } else {
                list += "," + i.next();
            }
            first = false;
        }

        protectedProperties.setProperty("protected", list);
        try {
            FileOutputStream out = new FileOutputStream("ProtectedMovies.mvd");
            protectedProperties.store(out, "---No Comment---");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveBackgroundFileloction() {
        Properties backgroundProperties = new Properties();
        String list = "";
        boolean first = true;
        HashMap imagetoCat = settings.getCategoryImageLocation();
        Set<String> e = imagetoCat.keySet();

        Iterator<String> i = e.iterator();

        while (i.hasNext()) {
            String key = i.next();
            backgroundProperties.setProperty(key, (String) imagetoCat.get(key));

        }

        try {
            FileOutputStream out = new FileOutputStream(backgroundImagesFile);
            backgroundProperties.store(out, "---No Comment---");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void saveNumberofViewings() {
        HashMap h = settings.getViewings();
        Set<String> e = h.keySet();
        Properties numberOfViewings = new Properties();
        String list = "";
        boolean first = true;

        Iterator<String> i = e.iterator();

        while (i.hasNext()) {
            String index = i.next();
            numberOfViewings.setProperty(index, h.get(index).toString());
        }
        try {
            FileOutputStream out = new FileOutputStream("NumberOfViewings.mvd");
            numberOfViewings.store(out, "---No Comment---");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void setVeiwerProgram(File f) {
        viewingProgram = f;
    }

    void setBackground(File f, String cat) {

        settings.addCategoryImageLocation(cat, f.getAbsolutePath());
    }

    private void showHide() {
        if (!showcat) {
            jScrollPane2.setVisible(true);
            categoryList.setSize(100, 700);
            categoryList.requestFocus();
            p.revalidate();
            p.repaint();
            showcat = true;
            jButton3.setText("HIDE");
        } else {
            JPanel p = (JPanel) this.getContentPane();
            jScrollPane2.setVisible(false);
            movieList.requestFocus();
            p.revalidate();
            p.repaint();
            showcat = false;
            jButton3.setText("SHOW");
        }
    }

    private void loadMovies() {
        try {
            //Thread.sleep(40000);
        } catch (Exception ex) {
        }
    }
}
