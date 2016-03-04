package com.demon.doubanmovies.model.bean;

import java.io.Serializable;

// 评分信息
public class RatingEntity implements Serializable {

    public int max;
    public double average;
    public String stars;
    public int min;
}
