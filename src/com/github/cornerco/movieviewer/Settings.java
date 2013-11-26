package com.github.cornerco.movieviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Settings {
    
    HashMap categories = new HashMap();
    private String startUpCategory = "";
    private HashMap protectedMovies = new HashMap();
    private HashMap viewings = new HashMap();
  //  private  ArrayList categoryImageLocation = new ArrayList();
    HashMap backgroundImages = new HashMap();

    public void print()
    {
       Collection e = categories.values();
       Iterator i = e.iterator();
       
       while(i.hasNext())
           {
           ArrayList v1 = (ArrayList) i.next();
           for(int x  =0; x<v1.size(); x++)
           {
               System.out.println(v1.get(x));
           }
       }
        
    }

    public String getBackgroundImage(String findString)
    {
       // System.out.println(backgroundImages);
        return (String)backgroundImages.get(findString);
    }

//     public int findIndex(String findString)
//    {
//        String[] categorisations = this.getAllCategories();
//         for(int c=0; c<categorisations.length; c++)
//        {
//            if(findString.equals(categorisations[c]))
//                return c;
//        }
//        return -1;
//    }



    public void addCategory(String s, ArrayList v)
    {
        categories.put(s, v);
    }
    
    public HashMap getprotectedMovies(){return protectedMovies;}
    public HashMap getViewings() {return viewings;}
    public int getTotalMoviesViewed(){return viewings.size();}
    
    public void updateViewings(String moviename)
    {
        Integer count =0;
        
        if(viewings.containsKey(moviename))
        {
             count = (Integer)viewings.get(moviename);
        }

        count++;
        
        viewings.put(moviename, count);
        
    }
    
    public void changeViewings(String moviename, int count)
    {
        if(count !=0)            
           viewings.put(moviename, count);
        else
           viewings.remove(moviename);
        
    }
    public int getViewings(String moviename)
    {
         Integer count = (Integer)viewings.get(moviename);
         if(count == null)
             count =0;
         return count;
        
    }
    
    public boolean checkProtected(String s)
    {
        return (protectedMovies.containsKey(s));
        
    }
    public void removeFolderFromCategory(String cat, File folder)
    {
        if(categories.containsKey(cat))
       {
            ArrayList list = (ArrayList) categories.get(cat);
            list.remove(folder);
       }

        
    }
    public void addFolderToCategory(String cat, File folder)
    {
       if(categories.containsKey(cat))
       {
            ArrayList list = (ArrayList) categories.get(cat);
           list.add(folder);
          // System.out.println("settings added "+ folder.toString() + " to list");
       }
       else
       {
           ArrayList v = new ArrayList();
           v.add(folder);
           categories.put(cat, v);
           
       }
           
        
    }
    
    public void renameCategory (String oldname, String newname)
    {
        
        ArrayList v = (ArrayList) categories.get(oldname);
        categories.remove(oldname);
        if(v!=null)
            categories.put(newname, v);
        
        
    }
    public boolean removeCategory(String s)
    {
        categories.remove(s);
        if(s.equals(startUpCategory))
        {
            return true;
            
        }
        return false;
        
    }
    
    public void addProctedMovie (File f)
    {
        protectedMovies.put(f.getAbsolutePath(), f);
    }
    
     public void removeProctedMovie (File f)
    {
        protectedMovies.remove(f.getAbsolutePath());
    }
     
    public ArrayList getCategory(String s)
    {
        return (ArrayList) (categories.get(s));
    }
    
    /* public ArrayList getCategory(String s)
    {
        return (ArrayList) (Categories.get(s));
    }*/
     
    public String[] getAllCategories()
    {
       // return 
       Set set = categories.keySet();
       String [] array = (String[])set.toArray(new String[set.size()]);
       Arrays.sort(array);
       return array;
        
    }
    public Set getCategoriesEnumerator()
    {
        return categories.keySet();        
    }

    public String getStartUpCategory() {
        return startUpCategory;
    }

    public void setStartUpCategory(String startUpCategory) {
        this.startUpCategory = startUpCategory;
    }

    /**
     * @return the categoryImageLocation
     */
    public HashMap getCategoryImageLocation() {
        return backgroundImages;
    }

    public void addCategoryImageLocation(String Category, String location) {
        backgroundImages.put(Category, location);
    }

  


    /**
     * @param categoryImageLocation the categoryImageLocation to set
     */
    public void setBackgroundImages(HashMap imageLocations) {
        this.backgroundImages = imageLocations;
    }

}
