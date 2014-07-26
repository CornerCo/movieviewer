package com.github.cornerco.movieviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Category {

    private final ArrayList<File> folders = new ArrayList<>();
    private final ArrayList<Movie> movies = new ArrayList<>();
    private String name;
    private String background;
    private boolean dirty = false;

    public Category(String name, String background) {
        Objects.requireNonNull(name);

        this.name = name;
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void invalidate() {
        dirty = true;
    }

    public void addFolder(File f) {
        if (!f.isDirectory()) {
            throw new RuntimeException(f.getAbsolutePath() + " is not a folder!");
        }

        folders.add(f);
        invalidate();
    }

    public void removeFolder(File f) {
        folders.remove(f);
        invalidate();
    }

    public boolean isDirty() {
        return dirty;
    }

    public List<File> getFolders() {
        return Collections.unmodifiableList(folders);
    }

    public List<Movie> getMovies() {
        refresh();

        synchronized (movies) {
            return Collections.unmodifiableList(movies);
        }
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void refresh() {
        if (!dirty) {
            return;
        }

        findMovies();

        dirty = false;
    }

    @Override
    public String toString() {
        return name;
    }

    private void findMovies() {
        synchronized (movies) {
            movies.clear();

            if (MovieManager.getInstance().isStarting()) {
                searchFolders();
            } else {
                final RefreshDialog dialog = new RefreshDialog();

                Thread worker = new Thread() {
                    @Override
                    public void run() {
                        searchFolders();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }

                        dialog.dispose();
                    }
                };

                worker.start();
                dialog.setVisible(true);

                while (worker.isAlive()) {
                    try {
                        worker.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Category.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void searchFolders() {
        for (File folder : folders) {
            ArrayList<File> files = new ArrayList<>();
            searchDirectory(files, folder);

            for (File movie : files) {
                movies.add(MovieManager.getInstance().createMovie(movie));
            }
        }
    }

    private void searchDirectory(ArrayList<File> movies, File entry) {
        if (!entry.isDirectory()) {
            if (Util.isMovieName(entry)) {
                movies.add(entry);
            }
        } else {
            for (File file : entry.listFiles()) {
                searchDirectory(movies, file);
            }
        }
    }
}
