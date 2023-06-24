package com.example.miniprojer02.db;

import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteQuotesContract {
    public static class Infos {
        public static final String TABLE_NAME = "favorite_quote";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_QUOTE = "quote";
        public static final String COLUMN_NAME_AUTHOR = "author";
    }

}
