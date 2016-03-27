package com.demon.doubanmovies.model.bean;

import java.io.Serializable;
import java.util.List;

// simple movie information
public class SimpleSubjectBean implements Serializable{

    public RatingEntity rating;
    public int collect_count;
    public String title;
    @SuppressWarnings("unused")
    public String original_title;
    @SuppressWarnings("unused")
    public String subtype;
    public String year;
    public ImagesEntity images;
    @SuppressWarnings("unused")
    public String alt;
    public String id;
    public List<String> genres;
    public List<CelebrityEntity> casts;
    public List<CelebrityEntity> directors;
}
