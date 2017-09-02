package com.ls.cookbook.util;

import android.text.TextUtils;
import android.util.Log;

import com.ls.cookbook.BuildConfig;

/**
 * Created by LS on 02.09.2017.
 */

public class Logger {


    public static boolean DEBUG = BuildConfig.DEBUG;
    private static final int STACK_TRACE_LEVELS_UP = 5;

    private static String LOG_TAG = "LOG_TAG";

    public static void i(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg))
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg))
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg))
            Log.d(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg))
            Log.w(tag, msg);
    }

    public static void i(String msg) {
        i(LOG_TAG, getClassName() + " " + msg);
    }

    public static void e(String msg) {
        e(LOG_TAG, getClassName() + " " + msg);

    }

    public static void d(String msg) {
        d(LOG_TAG, getClassName() + " " + msg);

    }

    public static void w(String msg) {
        w(LOG_TAG, getClassName() + " " + msg);
    }

    private static String getClassName() {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();
        if (fileName == null) {
            return "UNKNOWN Class name";
        } else {
            //Removing ".java" and returning class name
            return fileName.substring(0, fileName.length() - 5);
        }
    }
}
