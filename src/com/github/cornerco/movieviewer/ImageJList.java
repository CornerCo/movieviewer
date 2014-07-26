/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cornerco.movieviewer;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Peter Hoek
 */
public class ImageJList<E extends Object> extends JList<E> {

    private Image image;

    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
        }
        
        super.paintComponent(g);
    }

    public ImageJList(String image) {
        super();
        
        setOpaque(false);
        setImage(image);
    }

    public final void setImage(String image) {
        this.image = new ImageIcon(image).getImage();
        repaint();
    }

    public final Image getImage() {
        return image;
    }
}
