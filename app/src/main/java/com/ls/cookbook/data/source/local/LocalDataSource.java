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

import com.ls.cookbook.data.Task;
import com.ls.cookbook.data.source.DataSource;
import com.ls.cookbook.util.schedulers.BaseSchedulerProvider;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import com.ls.cookbook.data.source.local.LocalPersistenceContract.RecipeEntry;
import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class LocalDataSource implements DataSource {

    @Nullable
    private static LocalDataSource INSTANCE;

    @NonNull
    private final BriteDatabase mDatabaseHelper;

    @NonNull
    private Func1<Cursor, Task> mTaskMapperFunction;

    // Prevent direct instantiation.
    private LocalDataSource(@NonNull Context context,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "context cannot be null");
        checkNotNull(schedulerProvider, "scheduleProvider cannot be null");
        LocalDbHelper dbHelper = new LocalDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
        mTaskMapperFunction = this::getTask;
    }

    @NonNull
    private Task getTask(@NonNull Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME_ENTRY_ID));
        String title = c.getString(c.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME_TITLE));
        String description =
                c.getString(c.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME_DESCRIPTION));
        return new Task(title, description, itemId);
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
    public Observable<List<Task>> getTasks() {
        String[] projection = {
                RecipeEntry.COLUMN_NAME_ENTRY_ID,
                RecipeEntry.COLUMN_NAME_TITLE,
                RecipeEntry.COLUMN_NAME_DESCRIPTION
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), RecipeEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(RecipeEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        String[] projection = {
                RecipeEntry.COLUMN_NAME_ENTRY_ID,
                RecipeEntry.COLUMN_NAME_TITLE,
                RecipeEntry.COLUMN_NAME_DESCRIPTION
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), RecipeEntry.TABLE_NAME, RecipeEntry.COLUMN_NAME_ENTRY_ID);
        return mDatabaseHelper.createQuery(RecipeEntry.TABLE_NAME, sql, taskId)
                .mapToOneOrDefault(mTaskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(RecipeEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(RecipeEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        mDatabaseHelper.insert(RecipeEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();

        String selection = RecipeEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();

        String selection = RecipeEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link Repository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        mDatabaseHelper.delete(RecipeEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = RecipeEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
    }
}
