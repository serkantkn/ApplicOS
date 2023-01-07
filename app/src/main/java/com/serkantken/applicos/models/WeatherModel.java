package com.serkantken.applicos.models;

import java.io.Serializable;

public class WeatherModel implements Serializable
{
    long time;
    String cityName;
    double max_temp;
    double min_temp;
    int day_icon;
    int night_icon;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(double max_temp) {
        this.max_temp = max_temp;
    }

    public double getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(double min_temp) {
        this.min_temp = min_temp;
    }

    public int getDay_icon() {
        return day_icon;
    }

    public void setDay_icon(int day_icon) {
        this.day_icon = day_icon;
    }

    public int getNight_icon() {
        return night_icon;
    }

    public void setNight_icon(int night_icon) {
        this.night_icon = night_icon;
    }
}
