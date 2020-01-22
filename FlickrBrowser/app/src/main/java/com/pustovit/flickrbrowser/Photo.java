package com.pustovit.flickrbrowser;

import java.io.Serializable;

/**
 * Created by Pustovit Vladimir on 23.06.2019.
 * vovapust1989@gmail.com
 */

public class Photo implements Serializable {

    private static final long SerialVersionUID = 1L;
    private String title;
    private String author;
    private String authorId;
    private String img;
    private String tags;

     Photo(String title, String author, String authorId, String img, String tags) {
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.img = img;
        this.tags = tags;
    }

     String getTitle() {
        return title;
    }

     String getAuthor() {
        return author;
    }

     String getAuthorId() {
        return authorId;
    }

     String getSmallImage() {
        return img;
    }

     String getTags() {
        return tags;
    }

    String getBigImage() {
        return img.replaceFirst("_m.","_b.");
    }

    @Override
    public String toString() {
        return "\nPhoto" +
                "\ntitle=" + title +
                "\nauthor=" + author +
                "\nauthorId=" + authorId  +
                "\nsmall image=" + img +
                "\ntags=" + tags;
    }
}
