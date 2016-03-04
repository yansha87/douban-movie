package com.demon.doubanmovies.model.bean;


import java.io.Serializable;
import java.util.List;

// Top 250
public class CNMovieBean implements Serializable {
    public int start;
    public int count;
    public int total;
    public String title;
    public List<SimpleSubjectBean> subjects;
}
