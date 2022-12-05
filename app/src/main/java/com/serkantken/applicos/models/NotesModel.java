package com.serkantken.applicos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class NotesModel implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    int ID = 0;

    @ColumnInfo(name = "title")
    String title = "";

    @ColumnInfo(name = "notes")
    String notes = "";

    @ColumnInfo(name = "date")
    String date = "";

    @ColumnInfo(name = "color")
    int color = 0;

    @ColumnInfo(name = "pinned")
    boolean pinned = false;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
