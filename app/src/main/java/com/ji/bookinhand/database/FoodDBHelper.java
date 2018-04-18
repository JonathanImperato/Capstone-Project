package com.ji.bookinhand.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodDBHelper extends SQLiteOpenHelper {
    /**
     * PROJECT BASED ON UDACITY'S CONTENT PROVIDER PROJECT
     * https://github.com/udacity/android-content-provider
     */
    public static final String LOG_TAG = FoodDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 2;

    public FoodDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FOOD_TABLE = "CREATE TABLE " +
                ItemsContract.BookEntry.TABLE_NAME + "(" +
                ItemsContract.BookEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemsContract.BookEntry.COLUMN_TITLE +
                " TEXT NOT NULL, " +
                ItemsContract.BookEntry.COLUMN_AUTHORS +
                " TEXT, " +
                ItemsContract.BookEntry.COLUMN_AVERAGE_RATING +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_CANONICAL_VOLUME_LINK +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_DESCRIPTION +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_IMAGE_LINKS +
                " TEXT, " +
                ItemsContract.BookEntry.COLUMN_INFO_LINK +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_LANGUAGE +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_MATURITY_RATING +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_PREVIEW_LINK +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_PRINT_TYPE +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_PUBLISH_DATE +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_PUBLISHER +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_RATING_COUNT +
                " INTEGER , " +
                ItemsContract.BookEntry.COLUMN_SUBTITLE +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_CATEGORIES +
                " TEXT , " +
                ItemsContract.BookEntry.COLUMN_PAGE_COUNT +
                " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_FOOD_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        Log.w(LOG_TAG, "Upgrading database from version " + i + " to " +
                i1 + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemsContract.BookEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                ItemsContract.BookEntry.TABLE_NAME + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}