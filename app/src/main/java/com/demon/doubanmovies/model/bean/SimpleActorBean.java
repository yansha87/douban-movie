package com.demon.doubanmovies.model.bean;

public class SimpleActorBean {
    public CelebrityEntity entity;
    /**
     * 1. 导演
     * 2. 导演兼演员
     * 3. 演员
     */
    public int type;

    public SimpleActorBean(CelebrityEntity entity, int type) {
        this.entity = entity;
        this.type = type;
    }
}