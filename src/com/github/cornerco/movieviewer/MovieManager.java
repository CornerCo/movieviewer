package com.github.cornerco.movieviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieManager {

    private static volatile MovieManager instance;

    public static MovieManager getInstance() {
        if (instance == null) {
            instance = new MovieManager();
        }
        return instance;
    }
    private final Properties settings = new Properties();
    private final Properties movieViews = new Properties();
    private final Properties movieLocks = new Properties();
    private final HashMap<String, Category> categories = new HashMap<>();
    private boolean starting = true;
    private Category defaultCategory;
    private int totalMovies;
    private int totalMoviesViewed;

    private MovieManager() {
    }

    public void initialize() {
        loadMetadata();
        loadCategories();
        loadSettings();

        if (categories.isEmpty()) {
            addCategory(new Category("New Category", Util.getMovieBackgroundPath()));
        }

        starting = false;
    }

    public int getTotalMovies() {
        return totalMovies;
    }

    public int getTotalMoviesViewed() {
        return totalMoviesViewed;
    }

    public Category getDefaultCategory() {
        if (defaultCategory == null) {
            if (categories.isEmpty()) {
                return null;
            }

            defaultCategory = categories.values().iterator().next();
        }

        return defaultCategory;
    }

    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public Movie createMovie(File movie) {
        int views = Integer.parseInt(movieViews.getProperty(movie.getAbsolutePath(), "0"));
        boolean protect = Boolean.parseBoolean(movieLocks.getProperty(movie.getAbsolutePath(), "false"));

        return new Movie(Util.getFriendlyName(movie.getName()), movie, protect, views);
    }

    public void addMovieViewed() {
        totalMoviesViewed++;
    }

    public void removeMovieViewed() {
        totalMoviesViewed--;
    }

    private Category findCategory(String name) {
        return categories.get(name);
    }

    public void saveData() {
        try {
            saveMetadata();
        } catch (Exception e) {
        }

        try {
            saveCategories();
        } catch (Exception e) {
        }

        try {
            saveSettings();
        } catch (Exception e) {
        }
    }

    private void loadMetadata() {
        try {
            movieViews.load(new FileInputStream(Util.getMovieViewsFile()));
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            movieLocks.load(new FileInputStream(Util.getMovieLocksFile()));
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void pruneMetadata() {
        for (String movie : movieViews.stringPropertyNames()) {
            File f = new File(movie);
            if (!f.exists() || !f.isFile()) {
                movieViews.remove(movie);
            }
        }

        for (String movie : movieLocks.stringPropertyNames()) {
            File f = new File(movie);
            if (!f.exists() || !f.isFile()) {
                movieLocks.remove(movie);
            }
        }
    }

    public void saveMetadata() {
        for (Category c : new ArrayList<>(categories.values())) {
            for (Movie m : new ArrayList<>(c.getMovies())) {
                movieViews.setProperty(m.getFile().getAbsolutePath(), String.valueOf(m.getViews()));
                movieLocks.setProperty(m.getFile().getAbsolutePath(), String.valueOf(m.isProtected()));
            }
        }
        try {
            movieViews.store(new FileOutputStream(Util.getMovieViewsFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            movieLocks.store(new FileOutputStream(Util.getMovieLocksFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCategories() {
        Properties categoryFolders = new Properties();
        Properties categoryBackgrounds = new Properties();
        
        try {
            categoryFolders.load(new FileInputStream(Util.getCategoryFoldersFile()));
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            categoryBackgrounds.load(new FileInputStream(Util.getCategoryBackgroundsFile()));
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Map.Entry entry : categoryFolders.entrySet()) {
            String name = (String) entry.getKey();
            Category c = new Category(name, categoryBackgrounds.getProperty(name, Util.getMovieBackgroundPath()));

            for (String folder : ((String) entry.getValue()).split(",")) {
                if(folder.isEmpty()) {
                    continue;
                }
                
                c.addFolder(new File(folder));
            }

            addCategory(c);
        }
    }

    public void saveCategories() {
        Properties categoryFolders = new Properties();
        Properties categoryBackgrounds = new Properties();

        for (Category c : categories.values()) {
            StringBuilder folders = new StringBuilder();

            for (File folder : c.getFolders()) {
                folders.append(folder.getAbsolutePath());
                folders.append(",");
            }

            categoryFolders.setProperty(c.getName(), folders.toString());
            categoryBackgrounds.setProperty(c.getName(), c.getBackground());
        }
        
        try {        
            categoryFolders.store(new FileOutputStream(Util.getCategoryFoldersFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            categoryBackgrounds.store(new FileOutputStream(Util.getCategoryBackgroundsFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadSettings() {
        try {
            settings.load(new FileInputStream(Util.getSettingsFile()));
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        defaultCategory = findCategory(settings.getProperty("defaultCategory"));

        pruneMetadata();
        saveMetadata();
    }

    public void saveSettings() {
        settings.setProperty("defaultCategory", defaultCategory.getName());
        
        try {
            settings.store(new FileOutputStream(Util.getSettingsFile()), "");
        } catch (IOException ex) {
            Logger.getLogger(MovieManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void addCategory(Category category) {
        categories.put(category.getName(), category);

        for (Movie m : category.getMovies()) {
            totalMovies++;

            if (m.getViews() > 0) {
                totalMoviesViewed++;
            }
        }
    }

    public void removeCategory(Category category) {
        if (category == defaultCategory) {
            defaultCategory = null;
            getDefaultCategory();
        }

        for (Movie m : category.getMovies()) {
            totalMovies--;

            if (m.getViews() > 0) {
                totalMoviesViewed--;
            }
        }

        categories.remove(category.getName());
    }

    public Category getCategory(String name) {
        return categories.get(name);
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(new ArrayList<>(categories.values()));
    }

    public void terminate() {
        saveData();
        System.exit(0);
    }

    public boolean isStarting() {
        return starting;
    }
}
