package com.ls.cookbook.presenter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import com.kelvinapps.rxfirebase.RxFirebaseUser;
import com.ls.cookbook.CookBookApp;
import com.ls.cookbook.R;
import com.ls.cookbook.contract.LoginContract;
import com.ls.cookbook.util.Logger;
import com.ls.cookbook.util.ResourcesUtil;

import java.util.Arrays;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by LS on 02.09.2017.
 */

@SuppressWarnings("ALL")
public class LoginPresenter implements LoginContract.Presenter, GoogleApiClient.OnConnectionFailedListener {

    private LoginContract.View loginView;

    private FirebaseAuth mAuth;


    public LoginPresenter(LoginContract.View view) {
        this.loginView = view;
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void start() {
        trySignIn();
    }

    private void trySignIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loginView.onLoginSuccessful(currentUser);
        }
    }

    @Override
    public void loginEmail(String email, String password) {
        RxFirebaseAuth.signInWithEmailAndPassword(mAuth,email,password)
                .subscribe(token -> {
                    trySignIn();
                }, throwable -> {
                    loginView.onLoginFailure(throwable.getLocalizedMessage());
                });

//
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            trySignIn();
//                        } else {
//                            if (task.getException() != null) {
//                                loginView.onLoginFailure(task.getException().getLocalizedMessage());
//                            }
//                        }
//                    }
//                });
    }

    private void onComplete(Task <AuthResult> task) {
        if (task.isSuccessful()) {
            trySignIn();
        } else {
            if (task.getException() != null) {
                loginView.onLoginFailure(task.getException().getLocalizedMessage());
            }
        }

    }

    @Override
    public GoogleApiClient getGoogleApiClient(FragmentActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ResourcesUtil.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(CookBookApp.getContext())
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Logger.d("firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        RxFirebaseAuth.signInWithCredential(mAuth,credential)
                .subscribe(token -> {
                    trySignIn();
                }, throwable -> {
                    loginView.onLoginFailure(throwable.getLocalizedMessage());
                });
//
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Logger.d("signInWithCredential:success");
//                            trySignIn();
//                        } else {
//                            if (task.getException() != null) {
//                                loginView.onLoginFailure(task.getException().getLocalizedMessage());
//                            }
//                        }
//                    }
//                });
    }

    @Override
    public void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            loginView.onLoginFailure("Failure");
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
                loginView.onLoginFailure(exception.getLocalizedMessage());
            }
        });
        return callbackManager;
    }

    @Override
    public void loginFB(AppCompatActivity activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
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
                            trySignIn();
                        } else {
                            if (task.getException() != null) {
                                loginView.onLoginFailure(task.getException().getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        loginView.onLoginFailure(connectionResult.getErrorMessage());
    }
}
