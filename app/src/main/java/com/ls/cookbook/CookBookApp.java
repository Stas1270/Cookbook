package com.ls.cookbook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.ls.cookbook.util.Logger;
import com.ls.cookbook.util.UserHelper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;

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
        QBSettings.getInstance().init(getApplicationContext(), getString(R.string.qb_app_id), getString(R.string.qb_auth_key), getString(R.string.qb_auth_secret));
        QBSettings.getInstance().setAccountKey(getString(R.string.qb_account_key));
    }

    @NonNull
    public static Context getContext() {
        return sContext;
    }

}
