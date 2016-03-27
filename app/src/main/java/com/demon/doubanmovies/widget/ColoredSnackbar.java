package com.demon.doubanmovies.widget;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.demon.doubanmovies.R;

public class ColoredSnackbar {

    private static final int red = R.color.red_A200;
    private static final int green = R.color.green_A200;
    private static final int blue = R.color.blue_A200;
    private static final int orange = R.color.orange_A200;

    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Snackbar colorSnackBar(Snackbar snackbar, int colorId) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }

        return snackbar;
    }

    public static Snackbar info(Snackbar snackbar) {
        return colorSnackBar(snackbar, blue);
    }

    @SuppressWarnings("unused")
    public static Snackbar warning(Snackbar snackbar) {
        return colorSnackBar(snackbar, orange);
    }

    public static Snackbar alert(Snackbar snackbar) {
        return colorSnackBar(snackbar, red);
    }

    @SuppressWarnings("unused")
    public static Snackbar confirm(Snackbar snackbar) {
        return colorSnackBar(snackbar, green);
    }
}