package com.serkantken.applicos.telephone.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.serkantken.applicos.models.ContactModel;

import java.util.List;

@Dao
public interface MainDAO
{
    @Insert(onConflict = REPLACE)
    void insert(ContactModel contactModel);

    @Query("SELECT * FROM contacts ORDER BY id DESC")
    List<ContactModel> getAll();

    @Delete
    void delete(ContactModel contactModel);
}
