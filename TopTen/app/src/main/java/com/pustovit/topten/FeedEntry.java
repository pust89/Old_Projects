package com.pustovit.topten;


/**
 * Created by Pustovit Vladimir on 04.06.2019.
 * vovapust1989@gmail.com
 */

public class FeedEntry  {
    private String name;
    private String artist;
    private String summary;
    private String releaseDate ;
    private String imgUrl;

    public FeedEntry(String name, String artist, String summary, String releaseDate, String imgUrl) {
        this.name = name;
        this.artist = artist;
        this.summary = summary;
        this.releaseDate = releaseDate;
        this.imgUrl = imgUrl;
    }

    public FeedEntry(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String reasleaseDate) {
        this.releaseDate = reasleaseDate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return
                "name = " + name + '\n' +
                ", artist = " + artist + '\n' +
                ", summary = " + summary + '\n' +
                ", releaseDate = " + releaseDate + '\n' +
                ", imgUrl='" + imgUrl + '\n';
    }


}
