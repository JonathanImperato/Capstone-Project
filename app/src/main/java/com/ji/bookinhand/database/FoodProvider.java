package com.ji.bookinhand.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class FoodProvider extends ContentProvider {
    /**
     * PROJECT BASED ON UDACITY'S CONTENT PROVIDER PROJECT
     * https://github.com/udacity/android-content-provider
     **/
    private static final String LOG_TAG = FoodProvider.class.getSimpleName();
    //  private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FoodDBHelper mOpenHelper;

    private static final int FOOD = 50;
    private static final int INGREDIENT = 60;
    //private UriMatcher uriMatcher = buildUriMatcher();


    /*private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ItemsContract.CONTENT_AUTHORITY;
        // add a code for each type of URI you want
        matcher.addURI(authority, ItemsContract.BookEntry.TABLE_NAME, FOOD);
        return matcher;
    }*/

    @Override
    public boolean onCreate() {
        mOpenHelper = new FoodDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //   final int match = uriMatcher.match(uri);
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                ItemsContract.BookEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return ItemsContract.BookEntry.CONTENT_ITEM_FOOD;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        long _id = 0;

        _id = db.insert(ItemsContract.BookEntry.TABLE_NAME, null, values);
        // insert unless it is already contained in the database
        if (_id > 0) {
            returnUri = ItemsContract.BookEntry.buildFoodUriWithId(_id);
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("INSERTED DATA", " URI: " + returnUri);
            return returnUri;
        } else {
            throw new android.database.SQLException("Failed to insert row into: " + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //    final int match = sUriMatcher.match(uri);
        int numDeleted;
        //   final int match = uriMatcher.match(uri);


        numDeleted = db.delete(
                ItemsContract.BookEntry.TABLE_NAME, selection, selectionArgs);
        Log.w("CP", numDeleted + " INFO");
        return numDeleted;

    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        numUpdated = db.update(ItemsContract.BookEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);
        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;


    }
}