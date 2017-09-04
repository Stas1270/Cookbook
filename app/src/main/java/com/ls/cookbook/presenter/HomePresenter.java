package com.ls.cookbook.presenter;

import android.support.annotation.NonNull;

import com.ls.cookbook.contract.HomeContract;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.data.source.DataRepository;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;


/**
 * Created by LS on 02.09.2017.
 */

public class HomePresenter implements HomeContract.Presenter {
    @NonNull
    private DataRepository dataRepository;

    @NonNull
    private BaseSchedulerProvider baseSchedulerProvider;

    @NonNull
    private HomeContract.View homeView;

    @NonNull
    private ListCompositeDisposable disposableContainer;

    private boolean mFirstLoad = true;

    public HomePresenter(@NonNull DataRepository dataRepository, @NonNull HomeContract.View homeView, @NonNull BaseSchedulerProvider baseSchedulerProvider) {
        this.dataRepository = dataRepository;
        this.homeView = homeView;
        this.baseSchedulerProvider = baseSchedulerProvider;
        disposableContainer = new ListCompositeDisposable();
        homeView.setPresenter(this);
    }

    @Override
    public void getRecipeList(boolean forceUpdate) {
        loadRecipeList(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void addRecipe(Recipe recipe) {
        dataRepository.saveRecipe(recipe)
                .subscribe(next -> getRecipeList(false));
    }

    private void loadRecipeList(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            homeView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            dataRepository.refreshRecipeList();
        }
        Disposable disposable = dataRepository
                .getRecipeList()
                .subscribeOn(baseSchedulerProvider.computation())
                .observeOn(baseSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::processTasks,
                        // onError
                        throwable -> homeView.showLoadingError(),
                        // onCompleted
                        () -> homeView.setLoadingIndicator(false));
        disposableContainer.add(disposable);
    }

    private void processTasks(@NonNull List<Recipe> recipeList) {
        if (recipeList.isEmpty()) {
            homeView.showNoRecipes();
        } else {
            homeView.showRecipeList(recipeList);
        }
    }


    @Override
    public void subscribe() {
        getRecipeList(false);
    }

    @Override
    public void unsubscribe() {
        disposableContainer.dispose();
    }
}
