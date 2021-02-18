package com.musicstreaming.musicstreaming;

public class list_of_all_songs {

    String id,name,image,url,singer,bkcolor;
    public list_of_all_songs(String id,String name,String image,String url, String singer,String bkcolor) {

        this.id=id;
        this.name=name;
        this.image=image;
        this.url=url;
        this.singer=singer;
        this.bkcolor=bkcolor;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getBkcolor() {
        return bkcolor;
    }

    public void setBkcolor(String bkcolor) {
        this.bkcolor = bkcolor;
    }
}
