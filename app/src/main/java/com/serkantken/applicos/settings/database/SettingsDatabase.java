package com.serkantken.applicos.settings.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;

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

    public void editLongPreference(String preferenceName, String key, long value)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    public void editBooleanPreference(String preferenceName, String key, boolean value)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getStringPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "null");
    }

    public int getIntegerPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public long getLongPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

    public boolean getBooleanPreference(String preferenceName, String key)
    {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }
}
