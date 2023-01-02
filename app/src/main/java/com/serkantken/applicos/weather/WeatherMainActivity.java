package com.serkantken.applicos.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.serkantken.applicos.Constants;
import com.serkantken.applicos.R;
import com.serkantken.applicos.Utils;
import com.serkantken.applicos.databinding.ActivityWeatherMainBinding;
import com.serkantken.applicos.models.WeatherModel;
import com.serkantken.applicos.settings.database.SettingsDatabase;
import com.serkantken.applicos.weather.adapters.WeatherAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class WeatherMainActivity extends AppCompatActivity {
    private ActivityWeatherMainBinding binding;
    private static final int LOCATION_REQUEST_CODE = 1;
    private SettingsDatabase settingsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsDatabase = SettingsDatabase.getInstance(this);

        binding.buttonBack.setOnClickListener(v -> onBackPressed());
        Utils utils = new Utils(this, this);

        if (checkStoragePermission())
        {
            getUserWallpaper();
            utils.blur(binding.blurBackground, 5f, false);
            utils.blur(binding.actionBar, 10f, true);
        }
        else
        {
            requestStoragePermission();
        }

        ArrayList<WeatherModel> weatherList = new ArrayList<>();
        binding.todaysWeatherRV.setAdapter(new WeatherAdapter(this, weatherList));
        binding.todaysWeatherRV.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                if (isOneHourPassed())
                    if (Locale.getDefault().getLanguage().equals("tr"))
                    {
                        getCityName(location.getLongitude(), location.getLatitude(), Constants.lang_tr_TR);
                    }
                    else
                    {
                        getCityName(location.getLongitude(), location.getLatitude(), Constants.lang_en_US);
                    }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private boolean isOneHourPassed()
    {
        long currentDate = System.currentTimeMillis();
        long lastRefreshTime = settingsDatabase.getLongPreference(Constants.weatherPrefName, Constants.weatherRefreshTime);

        if (lastRefreshTime != 0) //Data kontrol
        {
            if (currentDate - lastRefreshTime < 3600 * 1000)
            {
                long remainingTime = 3600 * 1000 - (currentDate - lastRefreshTime);
                Toast.makeText(this, "Yenilemek için " + remainingTime + " saniye daha bekleyin", Toast.LENGTH_SHORT).show();
                binding.cityName.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_cityName));
                binding.temperature.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_temperature));
                binding.forecast.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_condition));
                Glide.with(this).load(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_icon)).into(binding.weatherAnim);

                binding.refreshTime.setText(String.format("%s%s", getString(R.string.last_update), TimeAgo.using(lastRefreshTime)));

                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    private void getCityName(double longitude, double latitude, String lang)
    {
        String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+ Constants.apiKey +"&q="+latitude+"%2C%20"+longitude+"&language="+lang;

        RequestQueue requestQueue = Volley.newRequestQueue(WeatherMainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String key = response.getString("Key");
                String cityName = response.getString("LocalizedName");
                binding.cityName.setText(cityName);
                settingsDatabase.editStringPreference(Constants.weatherPrefName, Constants.weather_cityName, cityName);
                getWeatherInfo(key, lang);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> binding.forecast.setText(error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getWeatherInfo(String cityName, String lang)
    {
        String url = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/"+cityName+"?apikey="+ Constants.apiKey +"&language="+lang+"&details=true&metric=true";

        RequestQueue requestQueue = Volley.newRequestQueue(WeatherMainActivity.this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try
            {
                JSONObject jsonObject = response.getJSONObject(0);

                String forecast = jsonObject.getString("IconPhrase");
                binding.forecast.setText(forecast);
                settingsDatabase.editStringPreference(Constants.weatherPrefName, Constants.weather_condition, forecast);

                String temperature = String.format("%s °C", jsonObject.getJSONObject("Temperature").getDouble("Value"));
                binding.temperature.setText(temperature);
                settingsDatabase.editStringPreference(Constants.weatherPrefName, Constants.weather_temperature, temperature);

                int iconNo = jsonObject.getInt("WeatherIcon");
                String eleman = String.valueOf(iconNo);
                String icon;
                if (eleman.length() == 2)
                {
                    icon = "https://developer.accuweather.com/sites/default/files/"+eleman+"-s.png";
                }
                else
                {
                    icon = "https://developer.accuweather.com/sites/default/files/0"+eleman+"-s.png";
                }
                Glide.with(this).load(icon).into(binding.weatherAnim);
                settingsDatabase.editStringPreference(Constants.weatherPrefName, Constants.weather_icon, icon);

                settingsDatabase.editLongPreference(Constants.weatherPrefName, Constants.weatherRefreshTime, System.currentTimeMillis());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> {
            //Toast.makeText(WeatherMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                if (checkStoragePermission())
                {
                    getUserWallpaper();
                }
            }
        }
        else
        {
            if (checkStoragePermission())
            {
                getUserWallpaper();
            }
        }
    });

    private void requestStoragePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            try
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }
            catch (Exception e)
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void getUserWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        @SuppressLint("MissingPermission") Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        binding.wallpaper.setBackground(wallpaperDrawable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}