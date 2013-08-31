/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.cornerco.movieviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Peter Hoek
 */
class MovieCellRenderer extends JLabel implements ListCellRenderer {
  private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

  public MovieCellRenderer() {
    setOpaque(true);
    setIconTextGap(12);
    
  }

  public Component getListCellRendererComponent(JList list, Object value,
    int index, boolean isSelected, boolean cellHasFocus) {
  
    MovieEntry entry = (MovieEntry) value;
    this.setFont(new java.awt.Font("Tahoma", 1, 12));
    int viewings = entry.getViewings();
    //setText(entry.getTitle() + " "+ viewings);
    setText(entry.getTitle());
   /* final ImageIcon imageIcon = entry.getImage();
    Image image = imageIcon.getImage();
    final Dimension dimension = this.getPreferredSize();
    final double height = dimension.getHeight();
    final double width = (height / imageIcon.getIconHeight()) * imageIcon.getIconWidth();
    image = image.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH);
    final ImageIcon finalIcon = new ImageIcon(image);
    setIcon(finalIcon);*/
    
    //setIcon(entry.getImage());
    if(entry.isProtected())
        setIcon(new ImageIcon("lock.gif"));
    else
        setIcon(new ImageIcon("cc.gif"));
    

    
    if (isSelected) {
        if(cellHasFocus)
        {
             if (viewings == 0)
              setBackground(Color.white);
             else if (viewings >=1 && viewings < 2)
              setBackground(Color.yellow);
             else if (viewings >= 2 && viewings < 5)
                setBackground(Color.orange);
             else if(viewings >=5 && viewings < 8)
                setBackground(Color.green);
             else if(viewings >= 8)
                setBackground(Color.cyan);

    
        
           setForeground(Color.black);
            
        }
        else
        {
              if (viewings == 0)
              setBackground(new Color(255,255,255,150));
             else if (viewings >=1 && viewings < 2)
              setBackground(new Color(255,255,255,150));
             else if (viewings >= 2 && viewings < 5)
                setBackground(new Color(255,215,0,150));
             else if(viewings >=5 && viewings < 8)
                setBackground(new Color(50,205,50,150));
             else if(viewings >= 8)
                setBackground(new Color(0,255,255,150));



           setForeground(Color.black);

        }
    } else {
        
        setBackground(new java.awt.Color(0, 0, 0,0));

        
        if (viewings == 0)
        {
            setForeground(Color.white);
           
        }   
        else if (viewings >=1 && viewings < 2)
              setForeground(Color.yellow);
        else if (viewings >= 2 && viewings < 5)
                setForeground(Color.orange);
        else if(viewings >=5 && viewings < 8)
                setForeground(Color.green);
        else if(viewings >= 8)
                setForeground(Color.cyan);

      
      
      
      
    }
    return this;
  }
  
  // override paintComponent method, enable antialiasing 
 /*   protected void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;

      // for antialiasing geometric shapes
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

      // for antialiasing text
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // to go for quality over speed
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);

      super.paintComponent(g2d);
    }  */
}