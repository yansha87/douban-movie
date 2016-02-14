package com.demon.doubanmovies.tryit;

import android.os.Parcelable;

/**
 * Created by user on 2015/12/27.
 */
public interface Query extends Parcelable {
    String getDescription();

    String getUrl();
}
