package com.ls.cookbook.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.ls.cookbook.CookBookApp;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by LS on 02.09.2017.
 */

public class ResourcesUtil {

    private static Context context = CookBookApp.getContext();
    private static Resources.Theme theme = CookBookApp.getContext().getTheme();

    public static Drawable getDrawableById(int resId) {
        return SDK_INT >= LOLLIPOP ? context.getResources().getDrawable(resId, theme) :
                context.getResources().getDrawable(resId);
    }

    public static ColorDrawable getColorDrawableById(int resId) {
        return new ColorDrawable(getColor(resId));
    }

    public static String getString(int resId) {
        return SDK_INT >= LOLLIPOP ? context.getResources().getString(resId) :
                context.getResources().getString(resId);
    }

    public static int getColor(int resId) {
        return SDK_INT >= Build.VERSION_CODES.M ? context.getResources().getColor(resId, theme) :
                context.getResources().getColor(resId);
    }
}
