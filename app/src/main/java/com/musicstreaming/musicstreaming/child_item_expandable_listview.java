package com.musicstreaming.musicstreaming;

public class child_item_expandable_listview {

    String id,username,name,playlist_name,status,image;

    public child_item_expandable_listview(String id,String username, String name, String playlist_name, String status,String image) {
        this.username = username;
        this.name = name;
        this.playlist_name = playlist_name;
        this.status = status;
        this.id=id;
        this.image=image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
