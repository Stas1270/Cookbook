package com.ls.cookbook.view;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.ls.cookbook.BaseActivity;
import com.ls.cookbook.R;
import com.ls.cookbook.contract.LoginContract;
import com.ls.cookbook.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A loginEmail screen that offers loginEmail via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    private static final int RC_SIGN_IN = 321;

//    @BindView(R.id.email)
//    EditText etEmail;
//
//    @BindView(R.id.password)
//    EditText etPassword;
//
//    @BindView(R.id.email_sign_in_button)
//    Button btnLogin;

    @BindView(R.id.login_via_google)
    SignInButton tvLoginGoogle;

    @BindView(R.id.login_via_FB)
    TextView tvLoginFB;

    private LoginContract.Presenter mLoginPresenter;
    private CallbackManager callbackManager;

    public static Intent getLoginActivityInstance(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public int initContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void setDataToActivityViews() {
        mLoginPresenter = new LoginPresenter(this);
        callbackManager = mLoginPresenter.registerFbManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.subscribe();
    }

    //    @OnClick(R.id.email_sign_in_button)
//    void onLoginButtonClick() {
//        String username = etEmail.getEditableText().toString();
//        String password = etPassword.getEditableText().toString();
//
//        if (TextUtils.isEmpty(username)) {
//            etEmail.setError(getString(R.string.msg_can_not_blank_username));
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError(getString(R.string.msg_can_not_blank_password));
//            return;
//        }
//        showProgressDialog();
//        mLoginPresenter.loginEmail(username, password);
//    }

    @OnClick(R.id.login_via_google)
    void onClickGoogleSignIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mLoginPresenter.getGoogleApiClient(this));
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.login_via_FB)
    void onClickFbSignIn() {
//        showProgressDialog();
        mLoginPresenter.loginFB(this);
    }

    @Override
    public void onLoginSuccessful() {
        startHomeScreen();
    }

    @Override
    public void onLoginFailure(String error) {
        dismissProgressDialog();
        showMessageOK(error, null);
    }

    @Override
    public void onLoginCancel() {
        dismissProgressDialog();
    }

    private void startHomeScreen() {
        dismissProgressDialog();
        startActivity(MainActivity.getMainActivityInstance(this));
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