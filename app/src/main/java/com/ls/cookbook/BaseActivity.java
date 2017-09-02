package com.ls.cookbook;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.ls.cookbook.util.Logger;
import com.ls.cookbook.util.ProgressUtil;
import com.ls.cookbook.util.ResourcesUtil;

import java.text.NumberFormat;
import java.util.List;

import butterknife.ButterKnife;
/**
 * Created by LS on 02.09.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected boolean isLoading;
    private ProgressDialog waitingDialog;
    private ProgressDialog waitingHorizontalDialog;
    private ProgressDialog progressDialog;

    public static final int PERMISSION_RECORD_AUDIO_REQUEST_CODE = 20;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 21;
    public static final int PERMISSION_GALLERY_REQUEST_CODE = 22;
    public static final int PERMISSION_CONTACTS_REQUEST_CODE = 23;
    public static final int PERMISSION_GPS_REQUEST_CODE = 24;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissWaitingDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idLayoutRes = initContentView();
        if (idLayoutRes > 0) {
            setContentView(idLayoutRes);
        }
        ButterKnife.bind(this);
        setDataToActivityViews();
    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//    }

    @LayoutRes
    public abstract int initContentView();

    public abstract void setDataToActivityViews();

    public void showWaitingDialog(String waitingMessage, boolean cancelable) {
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(this);
        }
        waitingDialog.setCancelable(cancelable);
        if (waitingMessage != null) {
            waitingDialog.setMessage(waitingMessage);
        }
        if (waitingDialog != null && !waitingDialog.isShowing()) {
            waitingDialog.show();
        }
    }

    public void showWaitingDialog(String waitingMessage) {
        showWaitingDialog(waitingMessage, false);
    }

    public void showWaitingDialog() {
        showWaitingDialog("");
    }

    public void dismissWaitingDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressUtil.createProgressDialog(this);
        }
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showWaitingHorizontalDialog(String message, boolean indeterminate) {
        showWaitingHorizontalDialog(ResourcesUtil.getString(R.string.please_wait), message, indeterminate);
    }

    public void showWaitingHorizontalDialog(String title, String message, boolean indeterminate) {
        if (waitingHorizontalDialog == null) {
            waitingHorizontalDialog = new ProgressDialog(this);
        }
        waitingHorizontalDialog.setIndeterminate(indeterminate);
        waitingHorizontalDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        waitingHorizontalDialog.setCancelable(false);
        waitingHorizontalDialog.setProgressNumberFormat(null);
        waitingHorizontalDialog.setMax(100);
        if (indeterminate) {
            waitingHorizontalDialog.setProgressPercentFormat(null);
        } else {
            waitingHorizontalDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
        }
        if (title != null) {
            waitingHorizontalDialog.setTitle(title);
        }
        if (message != null) {
            waitingHorizontalDialog.setMessage(message);
        }
        if (waitingHorizontalDialog != null && !waitingHorizontalDialog.isShowing()) {
            waitingHorizontalDialog.show();
        }
    }

    public void showWaitingHorizontalDialog(String title, String message) {
        showWaitingHorizontalDialog(title, message, false);
    }


    public void setWaitingHorizontalProgress(int progress) {
        if (waitingHorizontalDialog != null) {
            if (waitingHorizontalDialog.isShowing()) {
                waitingHorizontalDialog.setProgress(progress);
            }
        }
    }

    public void dismissWaitingHorizontalDialog() {
        if (waitingHorizontalDialog != null && waitingHorizontalDialog.isShowing()) {
            waitingHorizontalDialog.dismiss();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = this.getCurrentFocus();
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (!onBackWasPressed())
            super.onBackPressed();
    }

    public boolean onBackWasPressed() {
        boolean wasHandled = false;
        BaseFragment baseFragment = getVisibleFragment();
        if (baseFragment != null) {
//            wasHandled = baseFragment.onBackWasPressed();
        }
        return wasHandled;
    }

    public BaseFragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible() && fragment instanceof BaseFragment)
                    return (BaseFragment) fragment;
            }
        }
        return null;
    }

    public void onBackItemClick() {
        onBackPressed();
    }

    public void changeFragment(int layout, Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(layout, fragment);
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            fragmentTransaction.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestMultiplePermissions(int request) {
        int hasPermission = 0;
        String[] permission = null;
        switch (request) {
            case PERMISSION_CAMERA_REQUEST_CODE:
                hasPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
                permission = new String[]{android.Manifest.permission.CAMERA};
                break;

            case PERMISSION_RECORD_AUDIO_REQUEST_CODE:
                hasPermission = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
                permission = new String[]{android.Manifest.permission.RECORD_AUDIO};
                break;

            case PERMISSION_GALLERY_REQUEST_CODE:
                hasPermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permission = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                break;

            case PERMISSION_CONTACTS_REQUEST_CODE:
                hasPermission = checkSelfPermission(android.Manifest.permission.READ_CONTACTS);
                permission = new String[]{android.Manifest.permission.READ_CONTACTS};
                break;

            case PERMISSION_GPS_REQUEST_CODE:
                hasPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                permission = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};
                break;
        }

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permission, request);
        } else {
            onRequestAccessPermissionResult(request);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e(this.getClass().getSimpleName(), "onRequestPermissionsResult requestCode" + requestCode + ", permissions" + permissions.length);
        boolean grantResult = false;
        if (grantResults.length == 0) {
            return;
        }
        for (int res : grantResults) {
            if (res == PackageManager.PERMISSION_GRANTED) {
                grantResult = true;
            } else {
                grantResult = false;
                break;
            }
        }
        if (grantResult) {
            onRequestAccessPermissionResult(requestCode);
        } else {
            onRequestDeclinePermissionResult(requestCode);
            if (grantResults.length == 1 && !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                showMessageOKCancel("For using the app you need to allow access " + permissions[0] + ". Would you like to open settings to turn on?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    protected void onRequestDeclinePermissionResult(int requestCode) {

    }

    protected void onRequestAccessPermissionResult(int requestCode) {

    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), onClickListener)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create()
                .show();
    }

}