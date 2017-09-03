package com.ls.cookbook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by LS on 02.09.2017.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateView(inflater, container);
        if (view != null) {
            findFragmentViews(view, savedInstanceState);
            ButterKnife.bind(getActivity());
            setDataToFragmentViews();
        }
        return view;
    }

    @Nullable
    private View inflateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = null;
        int contentView = contentViewId();
        if (contentView > 0) {
            view = inflater.inflate(contentView, container, false);
        }
        return view;
    }

    @LayoutRes
    protected abstract int contentViewId();

    protected abstract void findFragmentViews(View view, Bundle savedInstanceState);

    protected abstract void setDataToFragmentViews();

    public void setIsLoading(boolean isLoading) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).setLoading(isLoading);
        }
    }

    public boolean isLoading() {
        boolean isLoading = false;
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            isLoading = ((BaseActivity) activity).isLoading();
        }
        return isLoading;
    }


    public void showWaitingDialog(String waitingMessage, boolean cancelable) {
        BaseActivity bActivity = getBaseActivity();
        if (bActivity != null) {
            bActivity.showWaitingDialog(waitingMessage, cancelable);
        }
    }

    public void showWaitingDialog(String waitingMessage) {
        BaseActivity bActivity = getBaseActivity();
        if (bActivity != null) {
            bActivity.showWaitingDialog(waitingMessage);
        }
    }

//    public void showWaitingDialog() {
//        BaseActivity bActivity = getBaseActivity();
//        if (bActivity != null) {
//            bActivity.showWaitingDialog();
//        }
//    }

    public void dismissWaitingDialog() {
        BaseActivity bActivity = getBaseActivity();
        if (bActivity != null) {
            bActivity.dismissWaitingDialog();
        }
    }

    public BaseActivity getBaseActivity() {
        BaseActivity bActivity = null;
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            bActivity = (BaseActivity) activity;
        }
        return bActivity;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    public void hideKeyboard() {
        BaseActivity bActivity = getBaseActivity();
        if (bActivity != null) {
            bActivity.hideKeyboard();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestMultiplePermissions(int request) {
        int hasPermission = 0;
        String[] permission = null;
        switch (request) {
            case BaseActivity.PERMISSION_CAMERA_REQUEST_CODE:
                hasPermission = getBaseActivity().checkSelfPermission(android.Manifest.permission.CAMERA);
                permission = new String[]{android.Manifest.permission.CAMERA};
                break;

            case BaseActivity.PERMISSION_RECORD_AUDIO_REQUEST_CODE:
                hasPermission = getBaseActivity().checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
                permission = new String[]{android.Manifest.permission.RECORD_AUDIO};
                break;

            case BaseActivity.PERMISSION_GALLERY_REQUEST_CODE:
                hasPermission = getBaseActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permission = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                break;

            case BaseActivity.PERMISSION_CONTACTS_REQUEST_CODE:
                hasPermission = getBaseActivity().checkSelfPermission(android.Manifest.permission.READ_CONTACTS);
                permission = new String[]{android.Manifest.permission.READ_CONTACTS};
                break;

            case BaseActivity.PERMISSION_GPS_REQUEST_CODE:
                hasPermission = getBaseActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(this.getClass().getSimpleName(), "onRequestPermissionsResult requestCode" + requestCode + ", permissions" + permissions.length);
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
            if (grantResults.length == 1 && !ActivityCompat.shouldShowRequestPermissionRationale(getBaseActivity(), permissions[0])) {
                getBaseActivity().showMessageOKCancel("For using the app you need to allow access " + permissions[0] + ". Would you like to open settings to turn on?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getBaseActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    protected void onRequestAccessPermissionResult(int requestCode) {

    }

    protected void onRequestDeclinePermissionResult(int requestCode) {

    }
}