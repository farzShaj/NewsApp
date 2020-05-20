package com.example.newsapp;

public class Source {
    String id="",name="",category="";

    Source(String id,String name,String category){
        this.name=name;
        this.id=id;
        this.category=category;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }
}
