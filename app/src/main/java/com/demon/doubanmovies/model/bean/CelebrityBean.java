package com.demon.doubanmovies.model.bean;

import java.util.List;

// 影人条目信息
public class CelebrityBean {

    public String mobile_url;         // 条目移动版url
    public String name;               // 中文名
    public String name_en;            // 英文名
    public String gender;             // 性别
    public ImagesEntity avatars;      // 影人头像
    public String alt;                // 条目页url
    public String born_place;         // 出生地
    public String id;                 // 条目id
    public List<String> aka_en;       // 更多英文名
    public List<String> aka;          // 更多中文名
    public List<WorksEntity> works;   // 影人作品,最多五部
}