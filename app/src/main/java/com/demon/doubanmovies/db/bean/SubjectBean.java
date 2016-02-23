package com.demon.doubanmovies.db.bean;

import java.util.List;

public class SubjectBean {

    public RatingEntity rating;        // 评分
    public int reviews_count;          // 影评数量
    public int wish_count;             // 想看人数
    public int collect_count;          // 看过人数
    public String douban_site;         // 豆瓣小站
    public String year;                // 年代
    public ImagesEntity images;        // 电影海报图，分别提供288px x 465px(大)，96px x 155px(中) 64px x 103px(小)尺寸
    public String alt;                 // 条目页URL
    public String id;                  // 条目id
    public String mobile_url;          // 移动版条目页URL
    public String title;               // 中文名
    public Object do_count;            // 在看人数，如果是电视剧，默认值为0，如果是电影值为null
    public Object seasons_count;       // 总季数(tv only)
    public String schedule_url;        // 影讯页URL(movie only)
    public Object episodes_count;      // 当前季的集数(tv only)
    public Object current_season;      // 当前季数(tv only)
    public String original_title;      // 原名
    public String summary;             // 简介
    public String subtype;             // 条目分类, movie或者tv
    public int comments_count;         // 短评数量
    public int ratings_count;          // 评分人数
    public List<String> genres;        // 影片类型，最多提供3个
    public List<String> countries;     // 制片国家/地区
    public List<CelebrityEntity> casts;// 主演，最多可获得4个，数据结构为影人的简化描述
    public List<CelebrityEntity> directors;// 导演，数据结构为影人的简化描述
    public List<String> aka;               // 又名
    public String localImageFile;
}
