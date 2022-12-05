package com.serkantken.applicos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "contacts")
public class ContactModel implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    int ID = 0;

    @ColumnInfo(name = "name")
    String name = "";

    @ColumnInfo(name = "phone")
    String phone = "";

    @ColumnInfo(name = "photo")
    String photo = "";

    @ColumnInfo(name = "email")
    String email = "";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
