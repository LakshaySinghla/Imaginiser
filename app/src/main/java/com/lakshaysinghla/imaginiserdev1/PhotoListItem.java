package com.lakshaysinghla.imaginiserdev1;

/**
 * Created by Lakshay Singhla on 10-Jul-17.
 */

public class PhotoListItem {

    private String name , path , size , createdAt;
    private int height , width;
    private boolean show = true;

    public void setName(String name){
        this.name = name;
    }
    public void setPath(String path){
        this.path = path;
    }
    public void setSize(String size){
        this.size = size;
    }
    public void setHeight(int h){
        this.height = h;
    }
    public void setWidth(int w){
        this.width = w;
    }
    public void setCreatedAt(String ca){
        this.createdAt = ca;
    }
    public void setShow(boolean show){
        this.show = show;
    }

    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
    public String getSize(){
        return size;
    }
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
    public String getCreatedAt(){
        return createdAt;
    }
    public boolean getShow(){
        return show;
    }
}
