package com.demon.doubanmovies.model.bean;

import java.util.List;

public class SubjectEntity {
    public RatingEntity rating;
    public int favorite_count;
    public String title;
    public String original_title;
    public String subtype;
    public String year;
    public ImagesEntity images;
    public String alt;
    public String id;
    public List<String> genres;
    public List<CastsEntity> casts;
    public List<DirectorsEntity> directors;

    public static class CastsEntity {
        public Object avatars;
        public Object alt;
        public Object id;
        public String name;
    }

    public static class DirectorsEntity {
        public Object avatars;
        public Object alt;
        public Object id;
        public String name;
    }

}
