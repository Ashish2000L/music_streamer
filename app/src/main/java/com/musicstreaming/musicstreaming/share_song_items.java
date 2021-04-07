package com.musicstreaming.musicstreaming;

public class share_song_items {

    String id,frdName,dateOfShare,songName,singerName,frdUserName,songId,songUrl, status;

    public share_song_items(String id, String frdName, String dateOfShare, String songName, String singerName, String frdUserName, String songId, String songUrl, String status){
        this.id = id;
        this.frdName = frdName;
        this.dateOfShare = dateOfShare;
        this.songName = songName;
        this.singerName = singerName;
        this.frdUserName = frdUserName;
        this.songId = songId;
        this.songUrl=songUrl;
        this.status=status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrdName() {
        return frdName;
    }

    public void setFrdName(String frdName) {
        this.frdName = frdName;
    }

    public String getDateOfShare() {
        return dateOfShare;
    }

    public void setDateOfShare(String dateOfShare) {
        this.dateOfShare = dateOfShare;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getFrdUserName() {
        return frdUserName;
    }

    public void setFrdUserName(String frdUserName) {
        this.frdUserName = frdUserName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
