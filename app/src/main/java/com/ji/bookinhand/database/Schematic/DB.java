package com.ji.bookinhand.database.Schematic;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


@Database(version = DB.VERSION)
public final class DB {

    public static final int VERSION = 1;

    @Table(ListColumns.class)
    public static final String BOOKS = "books";

}