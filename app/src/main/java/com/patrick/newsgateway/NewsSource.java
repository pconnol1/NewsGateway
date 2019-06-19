package com.patrick.newsgateway;

import android.support.annotation.NonNull;

import java.io.Serializable;


public class NewsSource
        implements Serializable, Comparable<NewsSource> {

    private String id;
    private String name;
    private String category;

    public NewsSource(String id, String name, String category){
        this.id= id;
        this.name=name;
        this.category=category;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof NewsSource)) return false;

        NewsSource ns = (NewsSource) o;

        return getName() != null ? getName().equals(ns.getName()) : ns.getName() == null;
    }
    @Override
    public int hashCode() { return getName() != null ? getName().hashCode() : 0; }

    @Override
    public int compareTo(@NonNull NewsSource ns) { return getName().compareTo(ns.getName());}



    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
