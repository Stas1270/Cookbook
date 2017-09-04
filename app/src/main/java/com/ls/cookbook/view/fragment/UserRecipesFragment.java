package com.ls.cookbook.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.ls.cookbook.BaseFragment;
import com.ls.cookbook.R;

/**
 * Created by LS on 04.09.2017.
 */

public class UserRecipesFragment extends BaseFragment {

    public static UserRecipesFragment newInstance() {
        return new UserRecipesFragment();
    }


    @Override
    protected int contentViewId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void findFragmentViews(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void setDataToFragmentViews() {

    }
}
