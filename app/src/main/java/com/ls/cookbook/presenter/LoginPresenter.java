package com.ls.cookbook.presenter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ls.cookbook.CookBookApp;
import com.ls.cookbook.contract.LoginContract;
import com.ls.cookbook.util.Logger;

/**
 * Created by LS on 02.09.2017.
 */

public class LoginPresenter implements LoginContract.Presenter, GoogleApiClient.OnConnectionFailedListener {

    LoginContract.View loginView;

    private FirebaseAuth mAuth;


    public LoginPresenter(LoginContract.View view) {
        this.loginView = view;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void start() {
        getSignedInEmailUser();
    }

    private void getSignedInEmailUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loginView.onLoginSuccessful(currentUser);
        }
    }

    @Override
    public void loginEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getSignedInEmailUser();
                        } else {
                            loginView.onLoginFailure();
                        }
                    }
                });
    }

    @Override
    public void loginFB(String email, String pwd) {

    }

    @Override
    public GoogleApiClient getGoogleApiClient(FragmentActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(CookBookApp.getContext())
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            loginView.onLoginSuccessful(acct);
        } else {
            loginView.onLoginFailure();
        }
    }

    @Override
    public CallbackManager registerFbManager() {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                loginView.onLoginCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                loginView.onLoginFailure();
            }
        });
        return callbackManager;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Logger.d("handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Logger.d("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginView.onLoginSuccessful(user);
                        } else {
                            loginView.onLoginFailure();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        loginView.onLoginFailure();
    }
}
