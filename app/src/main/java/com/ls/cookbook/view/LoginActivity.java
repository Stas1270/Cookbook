package com.ls.cookbook.view;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;
import com.ls.cookbook.BaseActivity;
import com.ls.cookbook.MainActivity;
import com.ls.cookbook.R;
import com.ls.cookbook.contract.LoginContract;
import com.ls.cookbook.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A loginEmail screen that offers loginEmail via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginContract.View{

    private static final int RC_SIGN_IN = 321;

    @BindView(R.id.email)
    EditText etEmail;

    @BindView(R.id.password)
    EditText etPassword;

    @BindView(R.id.email_sign_in_button)
    Button btnLogin;

    @BindView(R.id.login_via_google)
    SignInButton tvLoginGoogle;

    @BindView(R.id.login_via_FB)
    LoginButton tvLoginFB;

    private LoginContract.Presenter mLoginPresenter;
    private CallbackManager callbackManager;

    @Override
    public int initContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void setDataToActivityViews() {
        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.start();
        tvLoginFB.setReadPermissions("email", "public_profile");
        callbackManager = mLoginPresenter.registerFbManager();
    }

    @OnClick(R.id.email_sign_in_button)
    void onLoginButtonClick() {
        String username = etEmail.getEditableText().toString();
        String password = etPassword.getEditableText().toString();

        if (TextUtils.isEmpty(username)) {
            etEmail.setError(getString(R.string.msg_can_not_blank_username));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.msg_can_not_blank_password));
            return;
        }
        showWaitingDialog();
        mLoginPresenter.loginEmail(username, password);
    }
    @OnClick(R.id.login_via_google)
    void onClickGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mLoginPresenter.getGoogleApiClient(this));
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onLoginSuccessful(FirebaseUser currentUser) {
        startHomeScreen();
    }

    @Override
    public void onLoginSuccessful(GoogleSignInAccount acct) {
        startHomeScreen();
    }

    @Override
    public void onLoginFailure() {
        dismissWaitingDialog();
    }

    @Override
    public void onLoginCancel() {
        dismissWaitingDialog();
    }

    private void startHomeScreen() {
        dismissWaitingDialog();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mLoginPresenter.handleSignInResult(result);
        }
    }
}