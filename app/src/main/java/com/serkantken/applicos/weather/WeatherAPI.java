package com.serkantken.applicos.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.serkantken.applicos.R;
import com.serkantken.applicos.models.WeatherModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherAPI
{
    /*
    public void getWeatherInfo (Context context, String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=415b846a72184785817193613221011&q=" + cityName + "&days=1&aqi=yes&alerts=yes&lang=tr";

        binding.cityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            weatherList.clear();
            try {
                String temperature = response.getJSONObject("current").getString("temp_c");
                binding.temperature.setText(String.format("%s °C", temperature));
                int isDay = response.getJSONObject("current").getInt("is_day");
                String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                Glide.with(context).load("https:".concat(icon)).into(binding.weatherIcon);
                binding.forecast.setText(condition);
                if (isDay == 1) {
                    //Gündüz saati
                } else {
                    //Gece saati
                }

                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArray = forecastO.getJSONArray("hour");

                for (int i = 0; i < hourArray.length(); i++) {
                    WeatherModel model = new WeatherModel();
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    model.setTime(hourObj.getString("time"));
                    model.setTemperature(hourObj.getString("temp_c"));
                    model.setIcon(hourObj.getJSONObject("condition").getString("icon"));
                    model.setWindSpeed(hourObj.getString("wind_kph"));
                    weatherList.add(model);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "City name is not found. Change it from settings page", Toast.LENGTH_SHORT).show();
            binding.forecast.setText(error.getMessage());
        });
        requestQueue.add(jsonObjectRequest);
    }*/

    public String getCityName(Context context, double longitude, double latitude)
    {
        String cityName = "Not Found";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,10);

            for (Address address : addresses)
            {
                if (address != null)
                {
                    String city = address.getLocality();
                    if (city != null && !city.equals(""))
                    {
                        cityName = city;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /*public int selectWeatherAnimation(int icon, int isDay) {
        switch (icon)
        {
            case 1:
                return R.raw.weather_sunny;
            case 2:
                return R.raw.weather_sunny;
            case 3:
                return R.raw.weather_partly_cloudy;
            case 4:
                return R.raw.weather_partly_cloudy;
            case 5:
                return R.raw.weather_foggy;
            case 6:
            case 7:
            case 8:
                return R.raw.weather_cloudy;
            case 9:
                return R.raw.weather_mist;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:

        }
    }*/
}
