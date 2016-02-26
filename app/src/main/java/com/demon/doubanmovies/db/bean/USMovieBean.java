package com.demon.doubanmovies.db.bean;


import java.io.Serializable;
import java.util.List;

// Top 250
public class USMovieBean implements Serializable {
    public String date;
    public String title;
    public List<USSubjectBean> subjects;
}
