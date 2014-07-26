package com.github.cornerco.movieviewer;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Peter Hoek
 */
public class Movie {

    private final String name;
    private final File file;
    private boolean protect = false;
    private int views = 0;

    public Movie(String name, File file, boolean protect, int views) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(file);
        
        this.name = name;
        this.file = file;
        this.protect = protect;
        this.views = views;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {        
        if(this.views != views) {
            if(views == 0) {
                MovieManager.getInstance().removeMovieViewed();
            } else if(this.views == 0) {
                MovieManager.getInstance().addMovieViewed();                
            }
        }
        
        this.views = views;
    }

    public void setProtected(boolean value) {
        protect = value;
    }

    public boolean isProtected() {
        return protect;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public Path getPath() {
        return file.toPath();
    }
    
    @Override
    public String toString() {
        return getName();
    }
}