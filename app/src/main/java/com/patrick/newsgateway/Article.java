package com.patrick.newsgateway;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class Article
        implements Serializable {

    private String author;
    private String title;
    private String description;
    private String url;
    private transient String imageURL;
    private String publishedAt;

    public Article(String author, String title,String description, String url, String imageURL, String publishedAt){
        this.author=author;
        this.title=title;
        this.description= description;
        this.url=url;
        this.imageURL=imageURL;
        this.publishedAt=publishedAt;
    }

    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getImageURL() { return imageURL; }
    public String getPublishedAt() { return publishedAt; }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
