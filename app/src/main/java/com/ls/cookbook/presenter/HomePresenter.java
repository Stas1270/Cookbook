package com.ls.cookbook.presenter;

import android.support.annotation.NonNull;

import com.ls.cookbook.contract.HomeContract;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.data.source.Repository;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;


/**
 * Created by LS on 02.09.2017.
 */

public class HomePresenter implements HomeContract.Presenter {
    @NonNull
    private Repository repository;

    @NonNull
    private BaseSchedulerProvider baseSchedulerProvider;

    @NonNull
    private HomeContract.View homeView;

    @NonNull
    private ListCompositeDisposable disposableContainer;

    private boolean mFirstLoad = true;

    public HomePresenter(@NonNull Repository repository, @NonNull HomeContract.View homeView, @NonNull BaseSchedulerProvider baseSchedulerProvider) {
        this.repository = repository;
        this.homeView = homeView;
        this.baseSchedulerProvider = baseSchedulerProvider;
        disposableContainer = new ListCompositeDisposable();
    }

    @Override
    public void getRecipeList(boolean forceUpdate) {
        loadRecipeList(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadRecipeList(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            homeView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            repository.refreshRecipeList();
        }
        Disposable disposable = repository
                .getRecipeList()
                .subscribeOn(baseSchedulerProvider.computation())
                .observeOn(baseSchedulerProvider.ui())
                .doOnError(e -> homeView.showLoadingError())
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
        getRecipeList(true);
    }

    @Override
    public void unsubscribe() {
        disposableContainer.dispose();
    }
}
