package com.serkantken.applicos.models;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppModel implements Serializable
{
    private String name, packageName;
    private Drawable image;

    public AppModel(String name, String packageName, Drawable image) {
        this.name = name;
        this.packageName = packageName;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getImage() {
        return image;
    }
}
