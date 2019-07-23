package com.github.cornerco.movieviewer;

import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author khoek
 */
public class DebugExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler(new DebugExceptionHandler());
        System.setProperty("sun.awt.exception.handler", DebugExceptionHandler.class.getName());
    }

    private DebugExceptionHandler() {
    }

    // for EDT exceptions
    public void handle(Throwable thrown) {
        handleException(Thread.currentThread().getName(), thrown);
    }

    // for other uncaught exceptions
    @Override
    public void uncaughtException(Thread thread, Throwable thrown) {
        handleException(thread.getName(), thrown);
    }

    private void handleException(String threadName, Throwable e) {
        StringBuilder sb = new StringBuilder("Exception in thread '");
        sb.append(threadName);
        sb.append("': ");
        sb.append(e.getMessage());
        sb.append("\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString());
            sb.append("\n");
        }
        JTextArea jta = new JTextArea(sb.toString());
        JScrollPane jsp = new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
        JOptionPane.showMessageDialog(null, jsp, "Uncaught exception!", JOptionPane.ERROR_MESSAGE);
    }
}
