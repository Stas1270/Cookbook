package com.ls.cookbook.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ls.cookbook.R;
import com.ls.cookbook.util.Logger;
import com.ls.cookbook.util.UserHelper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

/**
 * Created by stanislav.safyanov on 11.09.17.
 */

public class SplashActivity  extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkQBSession();
    }

    private void checkQBSession() {
        QBAuth.getSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                showLoginScreen();
            }

            @Override
            public void onError(QBResponseException errors) {
                createQBSession();
            }
        });
    }

    private void createQBSession() {
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                showLoginScreen();
                Logger.d("createQBSession getToken: " + session.getToken());
                UserHelper.getInstance().setQBToken(session.getToken());
            }

            @Override
            public void onError(QBResponseException errors) {
                Logger.d("LocationUpdateService createQBSession ERROR:" + errors.toString());
            }
        } );
    }

    private void showLoginScreen() {
        startActivity(LoginActivity.getLoginActivityInstance(this));
    }

}
