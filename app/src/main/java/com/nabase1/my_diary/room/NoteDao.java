package com.nabase1.my_diary.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Notes note);

    @Update
    void update(Notes note);

    @Delete
    void delete(Notes note);

    @Query("Delete FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY timeStamp DESC")
    LiveData<List<Notes>> getAllNotes();

}
