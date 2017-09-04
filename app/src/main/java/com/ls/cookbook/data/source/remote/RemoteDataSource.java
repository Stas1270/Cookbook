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

package com.ls.cookbook.data.source.remote;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.data.source.DataSource;
import com.ls.cookbook.util.Logger;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE;

    private DatabaseReference mDatabase;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource() {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public Observable<List<Recipe>> getRecipeList() {
        return RxFirebaseDatabase
                .observeSingleValueEvent(getRecipeRef(), DataSnapshotMapper.listOf(Recipe.class))
                .doOnError(e-> Logger.e("error!!!"))
                .toObservable();
    }

    private DatabaseReference getRecipeRef() {
        return mDatabase.child("recipe");
    }

    @Override
    public Maybe<Recipe> getRecipe(@NonNull String taskId) {
        //// FIXME: 04.09.2017 implement rx
        return null;
//        return RxFirebaseDatabase
//                .observeSingleValueEvent(getRecipeRef(), DataSnapshotMapper.listOf(Recipe.class))
//                .toObservable();
    }

    @Override
    public void saveRecipe(@NonNull Recipe task) {
    }

    @Override
    public void deleteRecipe(@NonNull String id) {
    }

    @Override
    public void refreshRecipeList() {

    }
}
