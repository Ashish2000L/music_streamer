package com.example.musicstreaming;

import android.graphics.Bitmap;

public class track {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    private String title,album,url,imurl,like,bkcolor;
    public track(String title, String album,String url,String imgurl,String bkcolor,String like) {
        this.title = title;
        this.album = album;
        this.url = url;
        this.imurl=imgurl;
        this.bkcolor=bkcolor;
        this.like=like;
    }

    public String getBkcolor() {
        return bkcolor;
    }

    public void setBkcolor(String bkcolor) {
        this.bkcolor = bkcolor;
    }

    public String getImurl() {
        return imurl;
    }

    public void setImurl(String imurl) {
        this.imurl = imurl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }
}
