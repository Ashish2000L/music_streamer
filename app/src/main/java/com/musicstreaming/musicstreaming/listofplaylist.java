package com.musicstreaming.musicstreaming;

public class listofplaylist {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    String name,id,image,likes,note,totl_like;

    public listofplaylist(String id, String name, String image, String likes, String note, String totl_like) {
        this.name=name;
        this.id=id;
        this.image=image;
        this.likes=likes;
        this.note=note;
        this.totl_like=totl_like;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTotl_like() {
        return totl_like;
    }

    public void setTotl_like(String totl_like) {
        this.totl_like = totl_like;
    }
}
