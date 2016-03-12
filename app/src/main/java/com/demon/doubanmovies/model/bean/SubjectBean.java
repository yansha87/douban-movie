package com.demon.doubanmovies.model.bean;

import java.util.List;

// movie information
@SuppressWarnings("unused")
public class SubjectBean {

    public RatingEntity rating;        // rating
    public int reviews_count;          // review count
    public int wish_count;             // people want to watch
    public int collect_count;          // people have to watch
    public String douban_site;         // douban site
    public String year;                // movie year
    public ImagesEntity images;        // movie image，include 288px x 465px(large)，96px x 155px(medium) 64px x 103px(small)
    public String alt;                 // movie url
    public String id;                  // movie id
    public String mobile_url;          // mobile url
    public String title;               // China title
    public Object do_count;            // people at watching，default is 0 if tv，null if movie
    public Object seasons_count;       // season count(view_tag only)
    public String schedule_url;        // schedule url(movie only)
    public Object episodes_count;      // episode count(view_tag only)
    public Object current_season;      // current season number(view_tag only)
    public String original_title;      // origin title
    public String summary;             // summary
    public String subtype;             // movie type, movie or tv
    public int comments_count;         // comments count
    public int ratings_count;          // ratings count
    public List<String> genres;        // movie type，3 at most
    public List<String> countries;     // producer country/region
    public List<CelebrityEntity> casts;// actor, 4 at most
    public List<CelebrityEntity> directors;// director
    public List<String> aka;               // another name
    public String localImageFile;          // cache image file
}
