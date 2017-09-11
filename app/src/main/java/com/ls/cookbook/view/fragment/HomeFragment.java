package com.ls.cookbook.view.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ls.cookbook.BaseFragment;
import com.ls.cookbook.BasePresenter;
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
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by LS on 02.09.2017.
 */

public class HomeFragment extends BaseFragment implements OnListItemClickListener<Recipe>, HomeContract.View {

    @BindView(R.id.rv_home)
    RecyclerView recyclerView;

    @BindView(R.id.fab_add_recipe)
    FloatingActionButton fbAddRecipe;

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
//        homePresenter = new HomePresenter(Injection.provideTasksRepository(getApplicationContext()),
//                this,
//                Injection.provideSchedulerProvider());
        initAdapter();
//        homePresenter.subscribe();
    }

    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeListAdapter = new RecipeListAdapter(recipeList, this);
        recyclerView.setAdapter(recipeListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        homePresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homePresenter != null) homePresenter.unsubscribe();
    }

    @Override
    public void onItemClick(int position, Recipe recipe) {

    }

    @Override
    public void setLoadingIndicator(boolean b) {
        if (b) {
            showProgressDialog();
        } else {
            dismissProgressDialog();
        }
    }

    @Override
    public void showLoadingError() {
        dismissProgressDialog();
        showMessageOK("Error", null);
    }

    @Override
    public void showNoRecipes() {
        dismissProgressDialog();
        showMessageOK("Recipe list is empty", null);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        homePresenter = presenter;

    }

    @Override
    public void showRecipeList(List<Recipe> recipeList) {
        dismissProgressDialog();
        recipeListAdapter.setData(recipeList);
    }

    @OnClick(R.id.fab_add_recipe)
    void onClickAddRecipe() {
        long millis = System.currentTimeMillis();
        homePresenter.addRecipe(new Recipe(String.valueOf(millis), "name" + millis, "descr" + millis));
    }
}
