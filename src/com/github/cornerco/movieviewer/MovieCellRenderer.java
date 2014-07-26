/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cornerco.movieviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Peter Hoek
 */
public class MovieCellRenderer extends JLabel implements ListCellRenderer<Movie> {

    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    public MovieCellRenderer() {
        setOpaque(true);
        setIconTextGap(12);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Movie> list, Movie value, int index, boolean isSelected, boolean cellHasFocus) {
        Movie entry = (Movie) value;
        setFont(new Font("Tahoma", 1, 12));
        
        int viewings = entry.getViews();
        setText(entry.getName());

        if (entry.isProtected()) {
            setIcon(new ImageIcon(getClass().getResource("/com/github/cornerco/movieviewer/resources/lock.gif")));
        }

        if (isSelected) {
            if (cellHasFocus) {
                if (viewings == 0) {
                    setBackground(Color.white);
                } else if (viewings >= 1 && viewings < 2) {
                    setBackground(Color.yellow);
                } else if (viewings >= 2 && viewings < 5) {
                    setBackground(Color.orange);
                } else if (viewings >= 5 && viewings < 8) {
                    setBackground(Color.green);
                } else if (viewings >= 8) {
                    setBackground(Color.cyan);
                }

                setForeground(Color.black);
            } else {
                if (viewings == 0) {
                    setBackground(new Color(255, 255, 255, 150));
                } else if (viewings >= 1 && viewings < 2) {
                    setBackground(new Color(255, 255, 255, 150));
                } else if (viewings >= 2 && viewings < 5) {
                    setBackground(new Color(255, 215, 0, 150));
                } else if (viewings >= 5 && viewings < 8) {
                    setBackground(new Color(50, 205, 50, 150));
                } else if (viewings >= 8) {
                    setBackground(new Color(0, 255, 255, 150));
                }

                setForeground(Color.black);
            }
        } else {
            setBackground(new java.awt.Color(0, 0, 0, 0));

            if (viewings == 0) {
                setForeground(Color.white);
            } else if (viewings >= 1 && viewings < 2) {
                setForeground(Color.yellow);
            } else if (viewings >= 2 && viewings < 5) {
                setForeground(Color.orange);
            } else if (viewings >= 5 && viewings < 8) {
                setForeground(Color.green);
            } else if (viewings >= 8) {
                setForeground(Color.cyan);
            }

        }
        return this;
    }
}