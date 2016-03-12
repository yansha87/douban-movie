package com.demon.doubanmovies.model.bean;

import java.util.List;

// celebrity information
@SuppressWarnings("unused")
public class CelebrityBean {

    public String mobile_url;         // mobile url
    public String name;               // China name
    public String name_en;            // English name
    public String gender;             // gender
    public ImagesEntity avatars;      // celebrity image
    public String alt;                // celebrity url
    public String born_place;         // born place
    public String id;                 // celebrity id
    public List<String> aka_en;       // more English name
    public List<String> aka;          // more China name
    public List<WorksEntity> works;   // celebrity works, five at most
}
