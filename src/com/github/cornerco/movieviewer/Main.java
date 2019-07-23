package com.github.cornerco.movieviewer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author escortkeel
 */
public class Main {

    public static final int MAJOR = 2;
    public static final int MINOR = 0;
    public static final int REV = 0;

    public static void main(String args[]) throws IOException {
        DebugExceptionHandler.install();

        final File f = new File("movieviewer.lock");
        if (f.exists()) {
            f.delete();
        }

        final FileChannel channel = new RandomAccessFile(f, "rw").getChannel();
        final FileLock lock = channel.tryLock();
        if (lock == null) {
            channel.close();
            throw new RuntimeException("Only 1 instance of MovieViewer can run.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                MovieManager.getInstance().saveData();

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
                final MovieViewer viewer = new MovieViewer();
                final SplashFrame splash = new SplashFrame();
                splash.setVisible(true);

                new Thread("init") {
                    @Override
                    public void run() {
                        MovieManager.getInstance().initialize();
                        viewer.initialize();

                        splash.dispose();
                        viewer.setVisible(true);
                    }
                }.start();
            }
        });
    }
}
