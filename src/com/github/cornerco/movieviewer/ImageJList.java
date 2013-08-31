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
public class ImageJList extends JList
{
    private ImageIcon imageIcon = null;//new ImageIcon("test.gif");
    private Image image = null;//imageIcon.getImage();
    private Graphics g = null;
   // {setOpaque(false);} // instance initializer

    @Override
    public void paintComponent (Graphics g) {

        this.g = g;
        if(image!=null)
        {
            
            g.drawImage(getImage(), 0, 0, this.getWidth(), this.getHeight(), this);// draw to image
            
        }
        super.paintComponent(g);
    }

    public ImageJList (String imagelocation)
    {
        super();
      //  System.out.println("inside imagejlist "+imagelocation );
        this.setOpaque(false);
        imageIcon = new ImageIcon(imagelocation);
        image = imageIcon.getImage();
        repaint();
    }

    /**
     * @return the imageIcon
     */
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    /**
     * @param imageIcon the imageIcon to set
     */
    public void setImageIcon(String location) {
        imageIcon = new ImageIcon(location);
        image = imageIcon.getImage();
        this.repaint();
//        g.drawImage(getImage(), 0, 0, this);//chage draw to image
//        super.paintComponent(g);
    }

    /**
     * @return the image
     */
    public Image getImage() {
        image = imageIcon.getImage();
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }
}
