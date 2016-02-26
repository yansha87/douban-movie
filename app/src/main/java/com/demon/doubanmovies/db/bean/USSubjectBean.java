package com.demon.doubanmovies.db.bean;

import com.google.gson.annotations.SerializedName;

// 北美票房榜
public class USSubjectBean {

    public int box;
    @SerializedName("new")
    public boolean newX;
    public int rank;
    public SimpleSubjectBean subject;
}
