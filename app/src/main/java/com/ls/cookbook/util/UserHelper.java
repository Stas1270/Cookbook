package com.ls.cookbook.util;

/**
 * Created by LS on 04.09.2017.
 */


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ls.cookbook.CookBookApp;

public class UserHelper {

    private static final String SHARED_PREFERENCES_NAME = "cookbook";
    //Keys
    private static final String FB_TOKEN = "userToken";
    private static final String QB_TOKEN = "userToken";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    //Instance
    private static UserHelper instance = null;

    private UserHelper() {
        sharedPreferences = CookBookApp.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        return instance;
    }

    public void setQBToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QB_TOKEN, token);
        editor.apply();
    }

    public String getQBToken(){
        return sharedPreferences.getString(QB_TOKEN,"");
    }

    public void setFBToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FB_TOKEN, token);
        editor.apply();
    }

    public String getFBToken(){
        return sharedPreferences.getString(FB_TOKEN,"");
    }

    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QB_TOKEN);
        editor.remove(FB_TOKEN);
        editor.apply();
    }

}