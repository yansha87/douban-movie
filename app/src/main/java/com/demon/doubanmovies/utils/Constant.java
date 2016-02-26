package com.demon.doubanmovies.utils;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.db.bean.USSubjectBean;
import com.demon.doubanmovies.db.bean.CelebrityBean;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * 记录项目常量
 */
public class Constant {

    public static final HashMap<Integer, String> menuId2TitleDict = new HashMap<Integer, String>();
    public static final HashMap<Integer, String> title2TypeDict = new HashMap<Integer, String>();

    public static final String homepage = "首页";
    public static final String favorite = "收藏";
    public static final String setting = "设置";

    public static final int POS_IN_THEATERS = 0;
    public static final int POS_COMING = 1;
    public static final int POS_TOP250 = 2;
    public static final int POS_US_BOX = 3;

    public static final String API = "http://api.douban.com";
    public static final String IN_THEATERS = "in_theaters";
    public static final String US_BOX = "us_box";
    public static final String COMING = "coming_soon";
    public static final String TOP250 = "top250";
    public static final String SUBJECT = "/v2/movie/subject/";
    public static final String CELEBRITY = "/v2/movie/celebrity/";
    public static final String SEARCH_Q = "/v2/movie/search?q=";
    public static final String SEARCH_TAG = "/v2/movie/search?tag=";

    public static final Type subType = new TypeToken<SubjectBean>() {
    }.getType();

    public static final Type cleType = new TypeToken<CelebrityBean>() {
    }.getType();

    public static final Type simpleSubTypeList = new TypeToken<List<SimpleSubjectBean>>() {
    }.getType();

    public static final Type simpleBoxTypeList = new TypeToken<List<USSubjectBean>>() {
    }.getType();


    public static int[] menuIds = new int[]{
            R.id.nav_homepage,
            R.id.nav_favorite,
            R.id.nav_setting,
    };

    static {
        menuId2TitleDict.put(R.id.nav_homepage, Constant.homepage);
        menuId2TitleDict.put(R.id.nav_favorite, Constant.favorite);
        menuId2TitleDict.put(R.id.nav_setting, Constant.setting);
    }

    static {
        title2TypeDict.put(POS_IN_THEATERS, IN_THEATERS);
        title2TypeDict.put(POS_COMING, COMING);
        title2TypeDict.put(POS_TOP250, TOP250);
        title2TypeDict.put(POS_US_BOX, US_BOX);
    }
}
