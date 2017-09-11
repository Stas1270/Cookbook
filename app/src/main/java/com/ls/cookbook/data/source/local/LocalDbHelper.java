package com.ls.cookbook.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Cookbook.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_RECIPE_ENTRIES =
            "CREATE TABLE " + LocalPersistenceContract.RecipeEntry.TABLE_NAME + " (" +
                    LocalPersistenceContract.RecipeEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
//                    LocalPersistenceContract.RecipeEntry.COLUMN_DB_ID + TEXT_TYPE + COMMA_SEP +
                    LocalPersistenceContract.RecipeEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    LocalPersistenceContract.RecipeEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +
            " )";

    public LocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RECIPE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
