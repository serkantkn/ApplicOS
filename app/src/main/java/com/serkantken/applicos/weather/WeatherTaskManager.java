package com.serkantken.applicos.weather;

import android.os.AsyncTask;

import com.serkantken.applicos.models.WeatherModel;

public class WeatherTaskManager extends AsyncTask<String, Void, WeatherModel>
{

    @Override
    protected WeatherModel doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(WeatherModel s) {
        super.onPostExecute(s);
    }
}
