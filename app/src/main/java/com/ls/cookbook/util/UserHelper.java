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
    private static final String USER_TOKEN = "userToken";

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

    public void setToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public String getToken(){
        return sharedPreferences.getString(USER_TOKEN,"");
    }

    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_TOKEN);
        editor.apply();
    }

}