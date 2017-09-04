package com.ls.cookbook.data.source.local;

import android.provider.BaseColumns;

public final class LocalPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private LocalPersistenceContract() {}

    /* Inner class that defines the table contents */
    public static abstract class RecipeEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipe";
//        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }
}
