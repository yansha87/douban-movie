package com.demon.doubanmovies.model.bean;

public class SimpleActorBean {
    public CelebrityEntity entity;
    /**
     * 1. director
     * 2. director & actor
     * 3. actor
     */
    public int type;

    public SimpleActorBean(CelebrityEntity entity, int type) {
        this.entity = entity;
        this.type = type;
    }
}