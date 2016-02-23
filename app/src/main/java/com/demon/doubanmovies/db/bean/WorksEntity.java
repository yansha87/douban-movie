package com.demon.doubanmovies.db.bean;

import java.util.List;

public class WorksEntity {

    public SubjectEntity subject;
    public List<String> roles;

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public static class SubjectEntity {

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


        public static class RatingEntity {

            public int max;
            public double average;
            public String stars;
            public int min;
        }

        public static class ImagesEntity {

            public String small;
            public String large;
            public String medium;

        }

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
}
