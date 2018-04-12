package com.ji.bookinhand.database.Schematic;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = BooksProvider.AUTHORITY, database = DB.NotesDatabase.class)
public final class BooksProvider {

    public static final String AUTHORITY = "com.ji.bookinhand.database.Schematic.BooksProvider";

    @TableEndpoint(table = DB.NotesDatabase.LISTS)
    public static class Lists {
        @ContentUri(
                path = "lists",
                type = "vnd.android.cursor.dir/list",
                defaultSort = ListColumns.TITLE + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");
    }
}
