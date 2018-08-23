package com.nrv.pipdemo.model;

import java.io.Serializable;

public class VideoItem implements Serializable{
    private String title;
    private int resourceID;

    public static VideoItem newInstance(String title, int resourceID){
        return new VideoItem(title, resourceID);
    }

    public VideoItem(String title, int resourceID){
        this.title = title;
        this.resourceID = resourceID;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
