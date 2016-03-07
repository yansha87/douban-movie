package com.demon.doubanmovies.utils;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.model.bean.SimpleSubjectBean;
import com.demon.doubanmovies.model.bean.SubjectBean;
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

    public static final String HOMEPAGE = "首页";
    public static final String FAVORITE = "收藏";
    public static final String SETTING = "设置";

    public static final int POS_IN_THEATERS = 0;
    public static final int POS_COMING = 1;
    public static final int POS_TOP250 = 2;
    public static final int POS_US_BOX = 3;

    public static final String API = "http://api.douban.com";
    public static final String IN_THEATERS = "in_theaters";
    public static final String US_BOX = "us_box";
    public static final String COMING = "coming_soon";
    public static final String TOP250 = "top250";

    public static final String SIMPLE_SUBJECT_ID = "id";
    public static final String SIMPLE_SUBJECT_FOR = "favorite";

    public static final String SAVE_FAVORITE = "1";
    public static final String SAVE_COMMON = "0";

    public static final Type subType = new TypeToken<SubjectBean>() {
    }.getType();


    public static final Type simpleSubTypeList = new TypeToken<List<SimpleSubjectBean>>() {
    }.getType();


    public static int[] menuIds = new int[]{
            R.id.nav_homepage,
            R.id.nav_favorite,
            R.id.nav_setting,
    };

    static {
        menuId2TitleDict.put(R.id.nav_homepage, Constant.HOMEPAGE);
        menuId2TitleDict.put(R.id.nav_favorite, Constant.FAVORITE);
        menuId2TitleDict.put(R.id.nav_setting, Constant.SETTING);
    }

    static {
        title2TypeDict.put(POS_IN_THEATERS, IN_THEATERS);
        title2TypeDict.put(POS_COMING, COMING);
        title2TypeDict.put(POS_TOP250, TOP250);
        title2TypeDict.put(POS_US_BOX, US_BOX);
    }
}
