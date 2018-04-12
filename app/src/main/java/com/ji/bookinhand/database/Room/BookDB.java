package com.ji.bookinhand.database.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Book.class}, version = 1)
public abstract class BookDB extends RoomDatabase {
    public abstract BookDAO bookDAO();
}
