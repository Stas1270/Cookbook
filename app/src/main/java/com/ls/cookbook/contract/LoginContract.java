package com.ls.cookbook.contract;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.ls.cookbook.BasePresenter;

/**
 * Created by LS on 02.09.2017.
 */

public interface LoginContract {

    interface View {

        void onLoginSuccessful();


        void onLoginFailure(String error);

        void onLoginCancel();

    }

    interface Presenter extends BasePresenter {

        void loginEmail(String email, String pwd);

        void loginFB(AppCompatActivity appCompatActivity);

        GoogleApiClient getGoogleApiClient(FragmentActivity fragmentActivity);

        void handleSignInResult(GoogleSignInResult result);

        CallbackManager registerFbManager();
    }
}
