package com.example.newsapp;

import java.io.Serializable;

public class Article implements Serializable {
    String author,title,image_url,url,description,publishedAt;
    Article(){}
    Article(String author, String title,String image_url,String url,String description,String publishedAt){
        this.author=author;
        this.description=description;
        this.title=title;
        this.image_url=image_url;
        this.url=url;
        this.publishedAt=publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
