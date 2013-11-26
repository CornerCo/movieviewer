/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.cornerco.movieviewer;

import java.io.File;
import java.nio.file.Path;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;

/**
 *
 * @author Peter Hoek
 */
class MovieEntry implements java.lang.Comparable{
  private final String title;
  private final File file;
  private String sort = "Alphabetical";
 // private final String imagePath;

  private ImageIcon image;
  private boolean protectedValue = false;
  private int viewings =0;


    public MovieEntry(File f, String sort ) {
    this.file = f;    
    this.title = determineFileName();
    this.sort = sort;
    
    //this.imagePath = imagePath;
  }
  
  public int getViewings()
  {
      return viewings;
      
  }
  public void setViewings(int view)
  {
      viewings = view;
      
  }
  private String determineFileName()
  {
       String name=getFile().getAbsolutePath();
       return getDVDFileName(name);
  }
  
  public void setProtected(boolean value)
  {
      protectedValue = value;
      
  }
 
  public boolean isProtected()
  {
      
      return protectedValue;
  }
  private String getDVDFileName(String fileName)
  {
        String string2 = "";
        int index =0;
        fileName=fileName.toLowerCase();
        StringTokenizer st = new StringTokenizer(fileName,"\\");
        int tokencount = st.countTokens();
        
        String [] tokenArray = new String[tokencount];            
        for(int c =0; c< tokencount; c++)
        {
            string2 = st.nextToken();
            tokenArray[c] = string2;
            if (string2.equals("video_ts"))
                index = c;
        }
        
        if(fileName.endsWith("ifo"))
        {
            if(index != 0)
                return tokenArray[index-1];
            else return tokenArray[tokencount-2];
        }
        
        string2 = tokenArray[tokencount-1];
        index = string2.indexOf('.');
        return string2.substring(0,index);
   }

  public String getTitle() {
    return title;
  }

/*  public ImageIcon getImage() {
    //if (image == null) {
      image = new ImageIcon("lock.gif");
     /// System.out.println(image);
   // }
    return image;
  }*/

  // Override standard toString method to give a useful result
  public String toString() {
    return title;
  }

    public File getFile() {
        return file;
    }
    
    public Path getPath() {
        return file.toPath();
    }

    public int compareTo(Object o) {
        //final int BEFORE = -1;
        //final int EQUAL = 0;
        //final int AFTER = 1;
        MovieEntry me = (MovieEntry)o;
        if(sort.equals("Alphabetical"))
            return title.compareTo(me.title);
        else if(sort.equals("Viewings"))
        {
           Integer i1 = Integer.valueOf(this.viewings);
           Integer i2 = Integer.valueOf(me.getViewings());      
           return i1.compareTo(i2);
            
        }
        
        return 0;
            
    }
    
    
   
}