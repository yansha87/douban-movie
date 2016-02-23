package com.demon.doubanmovies.db.bean;

import com.google.gson.annotations.SerializedName;

public class BoxSubjectBean {

    public int box;
    @SerializedName("new")
    public boolean newX;
    public int rank;
    public SimpleSubjectBean subject;
}
