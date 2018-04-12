package com.ji.bookinhand.database.Schematic;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

public class DB {

    @Database(version = NotesDatabase.VERSION)
    public final class NotesDatabase {

        public static final int VERSION = 1;

        @Table(ListColumns.class)
        public static final String LISTS = "lists";
    }
}
