/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ls.cookbook.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.util.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class Repository implements DataSource {

    @Nullable
    private static Repository INSTANCE = null;

    @NonNull
    private final DataSource mTasksRemoteDataSource;

    @NonNull
    private final DataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    @Nullable
    Map<String, Recipe> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private Repository(@NonNull DataSource tasksRemoteDataSource,
                       @NonNull DataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link Repository} instance
     */
    public static Repository getInstance(@NonNull DataSource tasksRemoteDataSource,
                                         @NonNull DataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(tasksRemoteDataSource, tasksLocalDataSource);
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

        if (mCacheIsDirty) {
            return remoteTasks;
        } else {
            // Query the local storage if available. If not, query the network.
            Observable<List<Recipe>> localTasks = getAndCacheLocalTasks();
            return Observable.concat(localTasks, remoteTasks)
                    .filter(tasks -> !tasks.isEmpty())
                    .distinct();
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
                .doOnError(e-> Logger.e("ERROR REMOTE"));
    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {
        checkNotNull(recipe);
        mTasksRemoteDataSource.saveRecipe(recipe);
        mTasksLocalDataSource.saveRecipe(recipe);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(recipe.getId(), recipe);
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
                .doOnSuccess(task -> mCachedTasks.put(recipe, task));
    }

}
