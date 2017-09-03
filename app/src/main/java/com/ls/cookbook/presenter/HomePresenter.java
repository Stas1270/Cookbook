package com.ls.cookbook.presenter;

import android.support.annotation.NonNull;

import com.ls.cookbook.contract.HomeContract;
import com.ls.cookbook.data.source.Repository;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;

import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

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
    private CompositeSubscription mSubscriptions;


    public HomePresenter(@NonNull Repository repository, @NonNull HomeContract.View homeView, @NonNull BaseSchedulerProvider baseSchedulerProvider) {
        this.repository = repository;
        repository = checkNotNull(repository, "tasksRepository cannot be null");
        homeView = checkNotNull(homeView, "tasksView cannot be null!");
        baseSchedulerProvider = checkNotNull(baseSchedulerProvider, "schedulerProvider cannot be null");
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void start() {
        getRecipeList();
    }

    private void getRecipeList() {

    }

}
