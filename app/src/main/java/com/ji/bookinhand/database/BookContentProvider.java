package com.ji.bookinhand.database;


import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class BookContentProvider extends ContentProvider {

    /**
     * The authority of this content provider.
     */
    public static final String AUTHORITY = "com.ji.bookinhand.database.provider";

    String DB_NAME = "books-db";
    /**
     * The URI for the Book table.
     */
    public static final Uri URI_Book = Uri.parse(
            "content://" + AUTHORITY + "/" + Book.TABLE_NAME);

    /**
     * The match code for some items in the Book table.
     */
    private static final int CODE_Book_DIR = 1;

    /**
     * The match code for an item in the Book table.
     */
    private static final int CODE_Book_ITEM = 2;

    /**
     * The URI matcher.
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, Book.TABLE_NAME, CODE_Book_DIR);
        MATCHER.addURI(AUTHORITY, Book.TABLE_NAME + "/*", CODE_Book_ITEM);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = MATCHER.match(uri);
        if (code == CODE_Book_DIR || code == CODE_Book_ITEM) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            BookDAO Book = Room.databaseBuilder(getContext(),
                    BookDB.class, DB_NAME).build().bookDAO();

            final Cursor cursor;
            if (code == CODE_Book_DIR) {
                cursor = Book.getAll();
            } else {
                cursor = Book.findByTitle(selectionArgs[0]); //TODO: CHECK IF IT WORKS
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_Book_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Book.TABLE_NAME;
            case CODE_Book_ITEM:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + Book.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (MATCHER.match(uri)) {
            case CODE_Book_DIR:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                final Book book = Book.fromContentValues(values);
                final long id = Room.databaseBuilder(getContext(),
                        BookDB.class, DB_NAME).build().bookDAO().insert(book);
                context.getContentResolver().notifyChange(uri, null);
                Log.d("BOOK INSERTED", "id is: " + id);
                return ContentUris.withAppendedId(uri, id);
            case CODE_Book_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final Context context = getContext();
        switch (MATCHER.match(uri)) {
            case CODE_Book_DIR:
                if (context == null) {
                    return 0;
                }
                final int count1 = Room.databaseBuilder(getContext(),
                        BookDB.class, DB_NAME).build().bookDAO()
                        .deleteByTitle((selectionArgs[0]));
                context.getContentResolver().notifyChange(uri, null);
                return count1;

            case CODE_Book_ITEM:
                if (context == null) {
                    return 0;
                }
                final int count = Room.databaseBuilder(getContext(),
                        BookDB.class, DB_NAME).build().bookDAO()
                        .deleteByTitle((selectionArgs[0]));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case CODE_Book_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_Book_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final Book book = Book.fromContentValues(values);
                int count = Room.databaseBuilder(getContext(),
                        BookDB.class, DB_NAME).build().bookDAO()
                        .update(book); //TODO: CHECK IF IT WORKS
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (MATCHER.match(uri)) {
            case CODE_Book_DIR:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                BookDB database = Room.databaseBuilder(getContext(),
                        BookDB.class, DB_NAME).build();
                final Book[] Books = new Book[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    Books[i] = Book.fromContentValues(valuesArray[i]);
                }
                return database.bookDAO().insertAll(Books).length;
            case CODE_Book_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

}