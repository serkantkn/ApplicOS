package com.serkantken.applicos.telephone.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.serkantken.applicos.models.ContactModel;

@Database(entities = ContactModel.class, version = 1, exportSchema = false)
public abstract class ContactsDB extends RoomDatabase
{
    private static ContactsDB database;
    private static String DATABASE_NAME = "ContactsApp";

    public synchronized static ContactsDB getInstance(Context context)
    {
        if (database == null)
        {
            database = Room.databaseBuilder(context.getApplicationContext(), ContactsDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract MainDAO MainDao();
}
