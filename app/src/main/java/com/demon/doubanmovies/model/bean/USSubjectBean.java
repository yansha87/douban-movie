package com.demon.doubanmovies.model.bean;

import com.google.gson.annotations.SerializedName;

// American box
@SuppressWarnings("unused")
public class USSubjectBean {

    public int box;
    @SerializedName("new")
    public boolean newX;
    public int rank;
    public SimpleSubjectBean subject;
}
