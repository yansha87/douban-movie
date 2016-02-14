package com.demon.doubanmovies.tryit;

import android.os.Parcel;

/**
 * Created by user on 2015/12/27.
 */
public class RecentQuery implements Query {

    public static final Creator<RecentQuery> CREATOR = new Creator<RecentQuery>() {
        @Override
        public RecentQuery createFromParcel(Parcel source) {
            return RECENT_QUERY;
        }

        @Override
        public RecentQuery[] newArray(int size) {
            return new RecentQuery[size];
        }
    };

    private static final RecentQuery RECENT_QUERY = new RecentQuery();

    private RecentQuery() {

    }

    @Override
    public String getDescription() {
        return "Recent";
    }

    @Override
    public String getUrl() {
        return Api.getRecentUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static Query get() {
        return RECENT_QUERY;
    }
}
