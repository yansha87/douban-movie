package com.demon.doubanmovies.db.bean;


import java.io.Serializable;
import java.util.List;

// Top 250
public class TopBean implements Serializable {
    public int start;
    public int count;
    public int total;
    public String title;
    public List<SimpleSubjectBean> subjects;
}
