package com.demon.doubanmovies.model.bean;


import java.util.List;

// Top 250
public class CnMovieBean {
    public int start;
    @SuppressWarnings("unused")
    public int count;
    public int total;
    public String title;
    public List<SimpleSubjectBean> subjects;
}
