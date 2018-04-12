package com.ji.bookinhand.database.Schematic;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = BooksProvider.AUTHORITY,
        database = DB.class,
        packageName = "com.ji.bookinhand.database.Schematic.FoodProvider"
)
public final class BooksProvider {

    public static final String AUTHORITY = "com.ji.bookinhand.database.Schematic.FoodProvider";

    public BooksProvider() {
    }

    @TableEndpoint(table = DB.BOOKS)
    public static class Books {
        @ContentUri(
                path = "books",
                type = "vnd.android.cursor.dir/books",
                defaultSort = ListColumns.TITLE + " ASC")
        public static final Uri BOOKS = Uri.parse("content://" + AUTHORITY + "/books");
    }
}