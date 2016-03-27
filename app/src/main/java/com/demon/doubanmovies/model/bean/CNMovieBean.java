package com.demon.doubanmovies.model.bean;


import java.io.Serializable;
import java.util.List;

// Top 250
public class CnMovieBean implements Serializable {
    public int start;
    @SuppressWarnings("unused")
    public int count;
    public int total;
    public String title;
    public List<SimpleSubjectBean> subjects;
}
