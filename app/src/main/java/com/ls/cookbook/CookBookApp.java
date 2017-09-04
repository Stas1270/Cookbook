package com.ls.cookbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;

/**
 * Created by LS on 02.09.2017.
 */

public class CookBookApp extends MultiDexApplication {


    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        FirebaseApp.initializeApp(getApplicationContext());
    }

    @NonNull
    public static Context getContext() {
        return sContext;
    }
}
