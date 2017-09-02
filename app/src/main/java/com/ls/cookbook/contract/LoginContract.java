package com.ls.cookbook.contract;

import android.support.v4.app.FragmentActivity;

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

        void onLoginSuccessful(FirebaseUser currentUser);

        void onLoginSuccessful(GoogleSignInAccount acct);

        void onLoginFailure();

        void onLoginCancel();

    }

    interface Presenter extends BasePresenter {

        void loginEmail(String email, String pwd);

        void loginFB(String email, String pwd);

        GoogleApiClient getGoogleApiClient(FragmentActivity fragmentActivity);

        void handleSignInResult(GoogleSignInResult result);

        CallbackManager registerFbManager();
    }
}
