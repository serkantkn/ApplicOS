package com.serkantken.applicos.notes.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.serkantken.applicos.models.NotesModel;

import java.util.List;

@Dao
public interface MainDAO
{
    @Insert(onConflict = REPLACE)
    void insert(NotesModel notesModel);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<NotesModel> getAll();

    @Query("UPDATE notes SET title = :title, notes = :notes WHERE ID = :id")
    void  update(int id, String title, String notes);

    @Delete
    void delete(NotesModel notesModel);

    @Query("UPDATE notes SET pinned = :pin WHERE ID = :id")
    void pin(int id, boolean pin);
}
