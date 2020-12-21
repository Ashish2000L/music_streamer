package com.example.musicstreaming;

public class listofsongs {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    String name,url,image,likes,singer,bkcolor,id;
    public listofsongs(String id,String name,String url,String image,String likes,String singer,String bkcolor) {
        this.name=name;
        this.url=url;
        this.image=image;
        this.likes=likes;
        this.singer = singer;
        this.bkcolor=bkcolor;
        this.id=id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBkcolor() {
        return bkcolor;
    }

    public void setBkcolor(String bkcolor) {
        this.bkcolor = bkcolor;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
