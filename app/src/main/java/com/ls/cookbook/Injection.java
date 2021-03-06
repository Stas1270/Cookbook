package com.ls.cookbook;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ls.cookbook.data.source.Repository;
import com.ls.cookbook.data.source.local.LocalDataSource;
import com.ls.cookbook.data.source.remote.RemoteDataSource;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;
import com.ls.cookbook.util.schedulers.SchedulerProvider;


/**
 * Created by LS on 03.09.2017.
 */

public class Injection {

    public static Repository provideTasksRepository(@NonNull Context context) {
        return Repository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(context, provideSchedulerProvider()));
    }

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}
