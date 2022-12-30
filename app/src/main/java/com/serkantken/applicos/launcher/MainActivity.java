package com.serkantken.applicos.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.serkantken.applicos.launcher.adapters.LauncherPageAdapter;
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

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isEdgeToEdgeEnabled() == 0 || isEdgeToEdgeEnabled() == 1)
        {
            setMargins(binding.dockIcons, 0, 0, 0, 120);
        }
        else
        {
            setMargins(binding.dockIcons, 0, 0, 0, 70);
        }
        getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));

        blurViews(binding.dock);
        binding.pageViewer.setAdapter(new LauncherPageAdapter(MainActivity.this, 2));

        if (checkStoragePermission())
        {
            getUserWallpaper();
        }
        else
        {
            requestStoragePermission();
        }

        binding.iconInternet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
            startActivity(intent);
        });

        binding.iconTelephone.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TelephoneMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.iconTelephone, Objects.requireNonNull(ViewCompat.getTransitionName(binding.iconTelephone)));
            startActivity(intent, optionsCompat.toBundle());
        });

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
    }

    public void setMargins(View view, int start, int top, int end, int bottom)
    {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(start, top, end, bottom);
            view.requestLayout();
        }
    }

    public int isEdgeToEdgeEnabled()
    {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return resources.getInteger(resourceId);
        }
        return 0;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
            }
            else
            {
                Toast.makeText(MainActivity.this, "Permission is required for use this feature", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (checkStoragePermission())
        {
            getUserWallpaper();
        }
    }
}