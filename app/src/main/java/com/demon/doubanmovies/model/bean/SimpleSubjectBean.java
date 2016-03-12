package com.demon.doubanmovies.model.bean;

import java.io.Serializable;
import java.util.List;

// simple movie information
@SuppressWarnings("unused")
public class SimpleSubjectBean implements Serializable{

    public RatingEntity rating;
    public int collect_count;
    public String title;
    public String original_title;
    public String subtype;
    public String year;
    public ImagesEntity images;
    public String alt;
    public String id;
    public List<String> genres;
    public List<CelebrityEntity> casts;
    public List<CelebrityEntity> directors;
}
