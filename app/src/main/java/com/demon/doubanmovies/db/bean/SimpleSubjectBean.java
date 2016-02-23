package com.demon.doubanmovies.db.bean;

import java.util.List;

public class SimpleSubjectBean {

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
