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

package com.ls.cookbook.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ls.cookbook.data.model.Recipe;
import com.ls.cookbook.data.source.DataSource;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import com.ls.cookbook.data.source.local.LocalPersistenceContract.RecipeEntry;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * Concrete implementation of a data source as a db.
 */
public class LocalDataSource implements DataSource {

    @Nullable
    private static LocalDataSource INSTANCE;

    @NonNull
    private final BriteDatabase mDatabaseHelper;

    @NonNull
    private Function<Cursor, Recipe> mTaskMapperFunction;

    // Prevent direct instantiation.
    private LocalDataSource(@NonNull Context context,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        LocalDbHelper dbHelper = new LocalDbHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
        mTaskMapperFunction = this::getTask;
    }

    @NonNull
    private Recipe getTask(@NonNull Cursor c) {
        long itemId = c.getLong(c.getColumnIndexOrThrow(RecipeEntry._ID));
        String title = c.getString(c.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME_TITLE));
        String description =
                c.getString(c.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME_DESCRIPTION));
        return new Recipe(itemId, title, description);
    }

    public static LocalDataSource getInstance(
            @NonNull Context context,
            @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Maybe<Recipe> getRecipe(@NonNull String recipeId) {
        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_NAME_TITLE,
                RecipeEntry.COLUMN_NAME_DESCRIPTION
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), RecipeEntry.TABLE_NAME, RecipeEntry._ID);
        return mDatabaseHelper.createQuery(RecipeEntry.TABLE_NAME, sql, recipeId)
                .mapToOneOrDefault(mTaskMapperFunction, null).firstElement();
    }

    @Override
    public Observable<Recipe> saveRecipe(@NonNull Recipe recipe) {
        ContentValues values = new ContentValues();
//        values.put(RecipeEntry.COLUMN_NAME_ENTRY_ID, recipe.getId());
        values.put(RecipeEntry._ID, recipe.getId());
        values.put(RecipeEntry.COLUMN_NAME_TITLE, recipe.getName());
        values.put(RecipeEntry.COLUMN_NAME_DESCRIPTION, recipe.getDescription());
        long insert = mDatabaseHelper.insert(RecipeEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        return Observable.just(recipe);
    }

    @Override
    public void deleteRecipe(@NonNull String id) {
        String selection = RecipeEntry._ID + " LIKE ?";
        String[] selectionArgs = {id};
        mDatabaseHelper.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshRecipeList() {

    }

    @Override
    public Observable<List<Recipe>> getRecipeList() {
        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_NAME_TITLE,
                RecipeEntry.COLUMN_NAME_DESCRIPTION
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), RecipeEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(RecipeEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction);
    }
}
