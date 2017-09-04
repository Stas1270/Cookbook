package com.ls.cookbook.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.ls.cookbook.CookBookApp;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.util.InternetUtil;
import com.ls.cookbook.util.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class DataRepository implements DataSource {

    @Nullable
    private static DataRepository INSTANCE = null;

    @NonNull
    private final DataSource mTasksRemoteDataSource;

    @NonNull
    private final DataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    @Nullable
    Map<Long, Recipe> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private DataRepository(@NonNull DataSource tasksRemoteDataSource,
                           @NonNull DataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link DataRepository} instance
     */
    public static DataRepository getInstance(@NonNull DataSource tasksRemoteDataSource,
                                             @NonNull DataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DataSource, DataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     */

    @Override
    public Observable<List<Recipe>> getRecipeList() {
        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            return Observable.fromIterable(mCachedTasks.values()).toList().toObservable();
        } else if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Observable<List<Recipe>> remoteTasks = getAndSaveRemoteTasks();

        boolean connected = InternetUtil.isConnected(CookBookApp.getContext());

        Observable<List<Recipe>> localTasks = getAndCacheLocalTasks();
        if (!connected) {
            return localTasks;
        } else {
            if (mCacheIsDirty) {
                return remoteTasks;
            } else {
                // Query the local storage if available. If not, query the network.
                return Observable.concat(localTasks, remoteTasks)
                        .filter(tasks -> !tasks.isEmpty())
                        .distinct();
            }
        }
    }

    private Observable<List<Recipe>> getAndCacheLocalTasks() {
        return mTasksLocalDataSource.getRecipeList()
                .flatMap(recipes -> Observable.fromIterable(recipes)
                        .doOnNext(recipe -> mCachedTasks.put(recipe.getId(), recipe))
                        .toList()
                        .toObservable());
    }

    private Observable<List<Recipe>> getAndSaveRemoteTasks() {
        return mTasksRemoteDataSource
                .getRecipeList()
                .flatMap(new Function<List<Recipe>, Observable<List<Recipe>>>() {
                    @Override
                    public Observable<List<Recipe>> apply(@io.reactivex.annotations.NonNull List<Recipe> recipes) throws Exception {
                        return Observable.fromIterable(recipes).doOnNext(task -> {
                            mTasksLocalDataSource.saveRecipe(task);
                            mCachedTasks.put(task.getId(), task);
                        }).toList().toObservable();
                    }
                })
                .doOnComplete(() -> mCacheIsDirty = false)
                .doOnError(e -> Logger.e("ERROR REMOTE"));
    }

    @Override
    public Observable<Recipe> saveRecipe(@NonNull Recipe recipe) {
        checkNotNull(recipe);
        Observable<Recipe> observable = mTasksRemoteDataSource.saveRecipe(recipe);
        Observable<Recipe> saveRecipe = mTasksLocalDataSource.saveRecipe(recipe);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(recipe.getId(), recipe);
        return Observable.merge(observable,saveRecipe);
    }


    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     */
    @Override
    public Maybe<Recipe> getRecipe(@NonNull final String recipeId) {
        checkNotNull(recipeId);

        final Recipe cachedTask = getRecipeWithId(recipeId);

        // Respond immediately with cache if available
        if (cachedTask != null) {
            return Maybe.just(cachedTask);
        }

        // Load from server/persisted if needed.

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        // Is the task in the local data source? If not, query the network.
        Maybe<Recipe> localRecipe = getRecipeWithIdFromLocalRepository(recipeId);
        Maybe<Recipe> remoteRecipe = mTasksRemoteDataSource
                .getRecipe(recipeId)
                .doOnSuccess(recipe -> {
                    mTasksLocalDataSource.saveRecipe(recipe);
                    mCachedTasks.put(recipe.getId(), recipe);
                });

        return Maybe.concat(localRecipe, remoteRecipe)
                .firstElement()
                .map(task -> {
                    if (task == null) {
                        throw new NoSuchElementException("No recipe found with recipeId " + recipeId);
                    }
                    return task;
                });
    }

    @Override
    public void refreshRecipeList() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {
        mTasksRemoteDataSource.deleteRecipe(checkNotNull(recipeId));
        mTasksLocalDataSource.deleteRecipe(checkNotNull(recipeId));

        mCachedTasks.remove(recipeId);
    }

    @Nullable
    private Recipe getRecipeWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    @NonNull
    Maybe<Recipe> getRecipeWithIdFromLocalRepository(@NonNull final String recipe) {
        return mTasksLocalDataSource
                .getRecipe(recipe)
                .doOnSuccess(task -> mCachedTasks.put(Long.getLong(recipe), task));
    }

}
