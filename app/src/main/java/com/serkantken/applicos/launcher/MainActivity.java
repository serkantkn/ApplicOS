package com.serkantken.applicos.launcher;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.serkantken.applicos.clockalarm.ClockMainActivity;
import com.serkantken.applicos.databinding.ActivityMainBinding;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.models.NotesModel;
import com.serkantken.applicos.notes.NotesClickListener;
import com.serkantken.applicos.notes.NotesMainActivity;
import com.serkantken.applicos.notes.adapters.NotesListAdapter;
import com.serkantken.applicos.notes.database.RoomDB;
import com.serkantken.applicos.settings.database.SettingsDatabase;
import com.serkantken.applicos.telephone.TelephoneMainActivity;
import com.serkantken.applicos.telephone.database.ContactsDB;
import com.serkantken.applicos.weather.WeatherMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity implements NotesClickListener {
    private ActivityMainBinding binding;
    private RoomDB notesDatabase;
    private ContactsDB contactsDatabase;
    private List<NotesModel> notes = new ArrayList<>();
    private List<ContactModel> contacts = new ArrayList<>();
    private Location location;
    private SettingsDatabase settingsDatabase;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsDatabase = SettingsDatabase.getInstance(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkStoragePermission())
        {
            getUserWallpaper();
        }
        else
        {
            requestStoragePermission();
        }

        blurViews(binding.dock, binding.weatherContainer, binding.mediaContainer, binding.notesContainer, binding.contactsContainer);

        //SwipeRefreshLayout yükleme diskini kaldırma
        try {
            Field f = binding.notifGesture.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView imageView = (ImageView) f.get(binding.notifGesture);
            imageView.setAlpha(0.0f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        binding.notifGesture.setOnRefreshListener(this::showNotifications);

        loadNotesWidget();
        getFavouriteContacts();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            setWeatherWidget("huDDBDMjxyOxG2pxZSauU0uxY2Gbgwxa", location.getLongitude(), location.getLatitude(), "tr-TR");
        }
        //detectMediaPlaying();

        binding.profileImage.setOnClickListener(view -> {
            if (binding.username.getVisibility() == View.GONE) {
                binding.username.setVisibility(View.VISIBLE);
                binding.brightnessContainer.setVisibility(View.VISIBLE);
                binding.volumeContainer.setVisibility(View.VISIBLE);
                binding.clock.setVisibility(View.GONE);
            } else {
                binding.username.setVisibility(View.GONE);
                binding.brightnessContainer.setVisibility(View.GONE);
                binding.volumeContainer.setVisibility(View.GONE);
                binding.clock.setVisibility(View.VISIBLE);
            }
        });

        binding.clock.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ClockMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, binding.clock, Objects.requireNonNull(ViewCompat.getTransitionName(binding.clock)));
            startActivity(intent, optionsCompat.toBundle());
        });

        binding.iconInternet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
            startActivity(intent);
        });

        binding.notesTitle.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, binding.notesContainer, Objects.requireNonNull(ViewCompat.getTransitionName(binding.notesContainer)));
            startActivity(intent, optionsCompat.toBundle());
        });

        binding.addNote.setOnClickListener(v -> addNote());
        binding.iconAdd.setOnClickListener(v -> addNote());

        binding.iconTelephone.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TelephoneMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.iconTelephone, Objects.requireNonNull(ViewCompat.getTransitionName(binding.iconTelephone)));
            startActivity(intent, optionsCompat.toBundle());
        });

        binding.weatherContainer.setOnClickListener(v -> {
            if (binding.textWarning.getVisibility() == View.GONE)
            {
                Intent intent = new Intent(MainActivity.this, WeatherMainActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        new Pair<>(binding.weatherContainer, "weather"),
                        new Pair<>(binding.iconForecast, "weatherIcon"),
                        new Pair<>(binding.temperature, "temperature"),
                        new Pair<>(binding.weatherTitle, "cityName"),
                        new Pair<>(binding.condition, "condition"));
                startActivity(intent, optionsCompat.toBundle());
            }
            else
            {
                setWeatherWidget("huDDBDMjxyOxG2pxZSauU0uxY2Gbgwxa", location.getLongitude(), location.getLatitude(), "tr-TR");
            }
        });
    }

    private void addNote() {
        Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
        intent.putExtra("requestCode", "10");
        startActivity(intent);
    }

    private void loadNotesWidget() {
        notesDatabase = RoomDB.getInstance(this);
        notes = notesDatabase.mainDAO().getAll();
        List<NotesModel> pinnedNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).isPinned()) {
                pinnedNotes.add(notes.get(i));
            }
        }
        if (pinnedNotes.size() > 0) {
            binding.notesContainer.setVisibility(View.VISIBLE);
            binding.desktopNoteRV.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
            binding.desktopNoteRV.setAdapter(new NotesListAdapter(MainActivity.this, pinnedNotes, this));
        } else {
            binding.notesContainer.setVisibility(View.GONE);
        }

    }

    private void getFavouriteContacts() {
        contactsDatabase = ContactsDB.getInstance(this);
        contacts = contactsDatabase.MainDao().getAll();
        if (contacts.size() == 0) {
            binding.contactsContainer.setVisibility(View.GONE);
        } else {
            binding.contactsContainer.setVisibility(View.VISIBLE);
            binding.desktopContactRV.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
            binding.desktopContactRV.setAdapter(new FavouritesAdapter(MainActivity.this, contacts));
        }
    }

    private void setWeatherWidget(String apiKey, double longitude, double latitude, String lang)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        else
        {
            String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+apiKey+"&q="+latitude+"%2C%20"+longitude+"&language="+lang;

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    String key = response.getString("Key");
                    binding.weatherTitle.setText(response.getString("LocalizedName"));
                    getWeatherInfo(apiKey, key, lang);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }, error -> {
                binding.textWarning.setText(error.getMessage());
                binding.textWarning.setVisibility(View.VISIBLE);
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void getWeatherInfo (String apiKey, String cityName, String lang)
    {
        String url = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/"+cityName+"?apikey="+apiKey+"&language="+lang+"&details=true&metric=true";

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try
            {
                JSONObject jsonObject = response.getJSONObject(0);
                binding.condition.setText(jsonObject.getString("IconPhrase"));
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
                Glide.with(this).load(icon).into(binding.iconForecast);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(jsonObjectRequest);
    }

    /*private void detectMediaPlaying() {
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.music.metachanged");
            intentFilter.addAction("com.miui.player.metachanged");
            intentFilter.addAction("com.htc.music.metachanged");
            intentFilter.addAction("com.nullsoft.winamp.metachanged");
            intentFilter.addAction("com.real.IMP.metachanged");
            intentFilter.addAction("com.vanced.android.apps.youtube.music.metachanged");

            BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String cmd = intent.getStringExtra("command");

                    String artist = intent.getStringExtra("artist");
                    String album = intent.getStringExtra("album");
                    String track = intent.getStringExtra("track");
                    binding.artistName.setText(artist);
                    binding.songName.setText(track);
                }
            };

            registerReceiver(mediaReceiver, intentFilter);

            binding.mediaContainer.setVisibility(View.VISIBLE);
        }
    }*/

    @Override
    public void onClick(NotesModel notes) {
        Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
        intent.putExtra("requestCode", "11");
        intent.putExtra("note", notes);
        startActivity(intent);
    }

    @Override
    public void onLongClick(NotesModel notes, CardView cardView) {

    }

    private void blurViews(BlurView... blurView) {
        for (BlurView view : blurView) {
            blur(view, 10f, true);
        }
    }

    public void blur(BlurView view, float radius, boolean isRounded) {
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        view.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
        if (isRounded) {
            view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            view.setClipToOutline(true);
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    if (checkStoragePermission())
                    {
                        getUserWallpaper();
                    }
                }
            }
        }
        else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                binding.textWarning.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Permission is required for use this feature", Toast.LENGTH_SHORT).show();
                binding.textWarning.setVisibility(View.VISIBLE);
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

    private void showNotifications()
    {
        binding.notifGesture.setRefreshing(false);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            @SuppressLint("WrongConstant") Object service = getSystemService(STATUS_BAR_SERVICE);
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel");
            expand.invoke(service);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Expansion Not Working. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkStoragePermission())
        {
            getUserWallpaper();
        }
        loadNotesWidget();
        getFavouriteContacts();
        setWeatherWidget("huDDBDMjxyOxG2pxZSauU0uxY2Gbgwxa", location.getLongitude(), location.getLatitude(), "tr-TR");
        //detectMediaPlaying();
    }
}