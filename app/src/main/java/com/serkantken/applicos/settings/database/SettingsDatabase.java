package com.serkantken.applicos.settings.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsDatabase
{
    private static SettingsDatabase settingsDatabase = null;
    private SharedPreferences sharedPreferences;
    private final Context context;

    private SettingsDatabase(Context context)
    {
        this.context = context;
    }

    public static SettingsDatabase getInstance(Context context)
    {
        if (settingsDatabase == null)
        {
            settingsDatabase = new SettingsDatabase(context);
        }

        return settingsDatabase;
    }

    public void editStringPreference(String preferenceName, String key, String value)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public void editIntegerPreference(String preferenceName, String key, int value)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    private void editBooleanPreference(String preferenceName, String key, boolean value)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    private String getStringPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "null");
    }

    private int getIntegerPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    private boolean getBooleanPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }
}
