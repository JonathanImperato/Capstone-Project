package com.ji.bookinhand.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemsContract {

    /**
     * PROJECT BASED ON UDACITY'S CONTENT PROVIDER PROJECT
     * https://github.com/udacity/android-content-provider
     */
    public static final String CONTENT_AUTHORITY = "com.ji.bakingapp.database.FoodProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class BookEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "books";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHORS = "authors";
        public static final String COLUMN_PUBLISHER = "publisher";
        public static final String COLUMN_PUBLISH_DATE = "publishedDate";
        //  public static final String COLUMN_INDUSTRY_IDENTIFIERS = "industryIdentifiers";
        // public static final String COLUMN_READING_MODES = "readingModes";
        public static final String COLUMN_PAGE_COUNT = "pageCount";
        public static final String COLUMN_PRINT_TYPE = "printType";
        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_AVERAGE_RATING = "averageRating";
        public static final String COLUMN_RATING_COUNT = "ratingsCount";
        public static final String COLUMN_MATURITY_RATING = "maturityRating";
        public static final String COLUMN_IMAGE_LINKS = "imageLinks";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_PREVIEW_LINK = "previewLink";
        public static final String COLUMN_INFO_LINK = "infoLink";
        public static final String COLUMN_CANONICAL_VOLUME_LINK = "canonicalVolumeLink";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SUBTITLE = "subtitle";

        // create content uri
        public static final Uri CONTENT_URI_FOOD_TABLE = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_FOOD =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFoodUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_FOOD_TABLE, id);
        }


    }

}