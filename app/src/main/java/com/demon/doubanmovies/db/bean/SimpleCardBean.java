package com.demon.doubanmovies.db.bean;

public class SimpleCardBean {

    public String id;
    public String name;
    public String image;
    public Boolean isFilm;

    public SimpleCardBean(String id, String name, String medium, Boolean isFilm) {
        this.id = id;
        this.name = name;
        this.image = medium;
        this.isFilm = isFilm;
    }
}
