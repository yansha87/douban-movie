package com.demon.doubanmovies.model.bean;


import java.io.Serializable;
import java.util.List;

// Top 250
public class UsMovieBean implements Serializable {
    public String date;
    public String title;
    public List<UsSubjectBean> subjects;
}
