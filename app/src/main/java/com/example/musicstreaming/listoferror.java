package com.example.musicstreaming;

public class listoferror {

    String name,url,image,error_id,singer,bkcolor,id,status;
    public listoferror(String id,String name,String url,String image,String error_id,String singer,String bkcolor,String status) {
        this.name=name;
        this.url=url;
        this.image=image;
        this.error_id=error_id;
        this.singer = singer;
        this.bkcolor=bkcolor;
        this.id=id;
        this.status=status;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getError_id() {
        return error_id;
    }

    public void setError_id(String error_id) {
        this.error_id = error_id;
    }
}
