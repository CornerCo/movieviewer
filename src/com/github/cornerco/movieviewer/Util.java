package com.github.cornerco.movieviewer;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author escortkeel
 */
public class Util {

    private static final byte[] PASSWORDHASH = {
        111, -24, -20, -68, 29, -22, -6, 81,
        -62, -20, -16, -120, -49, 54, 78, -70,
        28, -21, -87, 3, 47, -5, -30, 98,
        30, 119, 27, -112, -22, -109, 21, 61};
    private static List<String> MOVIESUFFIXES = Arrays.asList(new String[]{
        "video_ts.ifo", "wmv", "avi", "mpg", "mpeg", "mod", "ts", "m2ts", "mkv", "mp4"
    });
    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static File ensureExists(String name) throws IOException {
        File f = new File(name);
        f.createNewFile();
        return f;
    }

    public static File getCategoryFoldersFile() throws IOException {
        return ensureExists("categoryFolders.mvd");
    }

    public static File getCategoryBackgroundsFile() throws IOException {
        return ensureExists("categoryBackgrounds.mvd");
    }

    public static File getSettingsFile() throws IOException {
        return ensureExists("settings.mvd");
    }

    public static File getMovieViewsFile() throws IOException {
        return ensureExists("movieViews.mvd");
    }

    public static File getMovieLocksFile() throws IOException {
        return ensureExists("movieLocks.mvd");
    }

    public static String getMovieBackgroundPath() {
        return "movieBackground.gif";
    }

    public static String getCategoryBackgroundPath() {
        return "categoryBackground.gif";
    }

    public static boolean isCorrectPassword(char[] raw) {
        byte[] bytes = digest.digest(new String(raw).getBytes());
        digest.reset();

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != PASSWORDHASH[i]) {
                return false;
            }
        }

        return true;
    }

    public static String getFriendlyName(File movie) {
        String name = movie.getName().toLowerCase();
        if (name.contains("video_ts")) {
            return getFriendlyName(movie.getParentFile());
        } else {
            int idx = name.indexOf(".");
            if (idx < 0) {
                idx = name.length();
            }

            return name.substring(0, idx).toLowerCase() + "  ";
        }
    }

    public static boolean isMovieName(File file) {
        for (String suffix : MOVIESUFFIXES) {
            if (file.getName().toLowerCase().endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }
}
