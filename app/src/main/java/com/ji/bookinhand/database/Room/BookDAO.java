package com.ji.bookinhand.database.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface BookDAO {

    @Query("SELECT * FROM books")
    Cursor getAll();

    @Query("SELECT * FROM books WHERE title LIKE :title")
    Cursor findByTitle(String title);

    @Query("SELECT * FROM books WHERE _ID LIKE :ID")
    Cursor findById(int ID);

    /**
     * TODO: FIX AUTHOR QUERY, IT WILL HAVE TO SHOW THE BOOK EVEN IF OTHER AUTHORS ARE IN THE BOOK AND ARE NOT IN THE PASSED AUTHORS PARAM
     * e.g. I Pass with "Calvino" only, I need to get all the books that contain this author
     *
     * @Query("SELECT * FROM books WHERE authors LIKE :authors ")
     * Book findByAuthor(String[] authors);
     */

    @Insert
    void insertAll(Book... users);

    @Delete
    void delete(Book user);

    @Query("DELETE FROM books WHERE _ID LIKE :ID")
    void deleteById(int ID);

    @Query("DELETE FROM books WHERE title LIKE :title")
    void deleteByTitle(String title);

    @Update
    void update(Book book);
}