package com.ls.cookbook.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ls.cookbook.BaseFragment;
import com.ls.cookbook.Injection;
import com.ls.cookbook.R;
import com.ls.cookbook.adapter.RecipeListAdapter;
import com.ls.cookbook.contract.HomeContract;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.interfaces.OnListItemClickListener;
import com.ls.cookbook.presenter.HomePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by LS on 02.09.2017.
 */

public class HomeFragment extends BaseFragment implements OnListItemClickListener<Recipe>, HomeContract.View {

    @BindView(R.id.rv_home)
    RecyclerView recyclerView;

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeListAdapter recipeListAdapter;

    private HomeContract.Presenter homePresenter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        initAdapter();
        homePresenter = new HomePresenter(Injection.provideTasksRepository(getApplicationContext()),
                this,
                Injection.provideSchedulerProvider());
        homePresenter.start();
    }

    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeListAdapter = new RecipeListAdapter(recipeList, this);
        recyclerView.setAdapter(recipeListAdapter);
    }

    @Override
    public void onItemClick(int position, Recipe recipe) {

    }
}
