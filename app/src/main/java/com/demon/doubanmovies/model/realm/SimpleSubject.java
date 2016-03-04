package com.demon.doubanmovies.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 简单电影条目
public class SimpleSubject extends RealmObject {
    @PrimaryKey
    private String id;
    private String favorite;
    private String jsonStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }
}
