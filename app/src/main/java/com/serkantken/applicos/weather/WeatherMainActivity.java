package com.serkantken.applicos.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.serkantken.applicos.databinding.ActivityWeatherMainBinding;
import com.serkantken.applicos.models.WeatherModel;
import com.serkantken.applicos.weather.adapters.WeatherAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class WeatherMainActivity extends AppCompatActivity {
    private ActivityWeatherMainBinding binding;
    private ArrayList<WeatherModel> weatherList;
    private WeatherAdapter adapter;
    private LocationManager locationManager;
    private static int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weatherList = new ArrayList<>();
        adapter = new WeatherAdapter(this, weatherList);
        binding.todaysWeatherRV.setAdapter(adapter);
        binding.todaysWeatherRV.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(WeatherMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else
            {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                getCityName("huDDBDMjxyOxG2pxZSauU0uxY2Gbgwxa", location.getLongitude(), location.getLatitude(), "tr-TR");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getCityName(String apiKey, double longitude, double latitude, String lang)
    {
        String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+apiKey+"&q="+latitude+"%2C%20"+longitude+"&language="+lang;

        RequestQueue requestQueue = Volley.newRequestQueue(WeatherMainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String key = response.getString("Key");
                binding.cityName.setText(response.getString("LocalizedName"));
                getWeatherInfo(apiKey, key, lang);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> {
            binding.forecast.setText(error.getMessage());
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getWeatherInfo (String apiKey, String cityName, String lang)
    {
        String url = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/"+cityName+"?apikey="+apiKey+"&language="+lang+"&details=true&metric=true";

        RequestQueue requestQueue = Volley.newRequestQueue(WeatherMainActivity.this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try
            {
                JSONObject jsonObject = response.getJSONObject(0);
                binding.forecast.setText(jsonObject.getString("IconPhrase"));
                binding.temperature.setText(String.format("%s °C", jsonObject.getJSONObject("Temperature").getDouble("Value")));
                int iconNo = jsonObject.getInt("WeatherIcon");
                String eleman = String.valueOf(iconNo);
                String icon = "";
                if (eleman.length() == 2)
                {
                    icon = "https://developer.accuweather.com/sites/default/files/"+eleman+"-s.png";
                }
                else
                {
                    icon = "https://developer.accuweather.com/sites/default/files/0"+eleman+"-s.png";
                }
                Glide.with(this).load(icon).into(binding.weatherAnim);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(WeatherMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(WeatherMainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(WeatherMainActivity.this, "Permission is required for use this feature", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void blur(BlurView view, float radius, boolean isRounded)
    {
        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        view.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
        if (isRounded)
        {
            view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            view.setClipToOutline(true);
        }
    }
}