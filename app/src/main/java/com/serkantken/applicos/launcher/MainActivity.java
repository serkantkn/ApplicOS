package com.serkantken.applicos.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.orhanobut.hawk.Hawk;
import com.serkantken.applicos.R;
import com.serkantken.applicos.clockalarm.ClockMainActivity;
import com.serkantken.applicos.databinding.ActivityMainBinding;
import com.serkantken.applicos.launcher.adapters.LauncherPageAdapter;
import com.serkantken.applicos.models.AppModel;
import com.serkantken.applicos.notes.NotesMainActivity;
import com.serkantken.applicos.settings.database.SettingsDatabase;
import com.serkantken.applicos.telephone.TelephoneMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BottomSheetBehavior<BlurView> bottomSheetBehavior;
    private SettingsDatabase database;
    private List<AppModel> installedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setTheme(R.style.Theme_ApplicOS);
        setContentView(binding.getRoot());

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_transparent));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        database = SettingsDatabase.getInstance(this);
        Hawk.init(this).build();

        installedApps = getInstalledApps();
        initializeDock();
        LauncherPageAdapter adapter = new LauncherPageAdapter(MainActivity.this, installedApps, 2);
        binding.pageViewer.setAdapter(adapter);

        binding.icon1.setOnClickListener(v -> {
            Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(Hawk.get("dockIcon1"));
            if (launchAppIntent != null) {
                startActivity(launchAppIntent);
            }
        });
        binding.icon2.setOnClickListener(v -> {
            Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(Hawk.get("dockIcon2"));
            if (launchAppIntent != null) {
                startActivity(launchAppIntent);
            }
        });
        binding.icon3.setOnClickListener(v -> {
            Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(Hawk.get("dockIcon3"));
            if (launchAppIntent != null) {
                startActivity(launchAppIntent);
            }
        });
        binding.icon4.setOnClickListener(v -> {
            Intent launchAppIntent = getPackageManager().getLaunchIntentForPackage(Hawk.get("dockIcon4"));
            if (launchAppIntent != null) {
                startActivity(launchAppIntent);
            }
        });

        if (checkStoragePermission()) {
            getUserWallpaper();
        } else {
            requestStoragePermission();
        }

        binding.buttonNotes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
            startActivity(intent);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    private List<AppModel> getInstalledApps()
    {
        List<AppModel> list = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> untreatedAppList = getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo app : untreatedAppList)
        {
            String appName = app.loadLabel(getPackageManager()).toString();
            String appPackageName = app.activityInfo.packageName;
            Drawable appImage = app.activityInfo.loadIcon(getPackageManager());
            AppModel appModel = new AppModel(appName, appPackageName, appImage);
            if (!list.contains(appModel))
            {
                list.add(appModel);
            }
        }
        return list;
    }

    private void initializeDock() {
        blurViews(binding.dock);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.dock);
        bottomSheetBehavior.setHideable(false);
        if (isEdgeToEdgeEnabled() == 0 || isEdgeToEdgeEnabled() == 1) {
            bottomSheetBehavior.setPeekHeight(310);
        } else {
            bottomSheetBehavior.setPeekHeight(250);
        }

        String package1 = "", package2 = "", package3 = "", package4 = "";
        if (Hawk.contains("dockIcon1")) {
            package1 = Hawk.get("dockIcon1");
        } else if (Hawk.contains("dockIcon2")){
            package2 = Hawk.get("dockIcon2");
        } else if (Hawk.contains("dockIcon3")){
            package3 = Hawk.get("dockIcon3");
        } else if (Hawk.contains("dockIcon4")){
            package4 = Hawk.get("dockIcon4");
        }

        for (AppModel model : installedApps)
        {
            if (package1.equals(model.getPackageName())) {
                Glide.with(this).load(model.getImage()).into(binding.icon1);
            } else if (package2.equals(model.getPackageName())) {
                Glide.with(this).load(model.getImage()).into(binding.icon2);
            } else if (package3.equals(model.getPackageName())) {
                Glide.with(this).load(model.getImage()).into(binding.icon3);
            } else if (package4.equals(model.getPackageName())){
                Glide.with(this).load(model.getImage()).into(binding.icon4);
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
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

    public void setDockIcons(List<AppModel> list, AppModel selected)
    {
        String package1 = "", package2 = "", package3 = "", package4 = "";
        if (Hawk.contains("dockIcon1")) {
            package1 = Hawk.get("dockIcon1");
        } else if (Hawk.contains("dockIcon2")){
            package2 = Hawk.get("dockIcon2");
        } else if (Hawk.contains("dockIcon3")){
            package3 = Hawk.get("dockIcon3");
        } else if (Hawk.contains("dockIcon4")){
            package4 = Hawk.get("dockIcon4");
        }

        if (list.contains(selected))
        {
            for (AppModel model : list)
            {
                if (package1.equals(model.getPackageName())) {
                    Glide.with(getApplicationContext()).load(model.getImage()).into(binding.icon1);
                } else if (package2.equals(model.getPackageName())) {
                    Glide.with(getApplicationContext()).load(model.getImage()).into(binding.icon2);
                } else if (package3.equals(model.getPackageName())) {
                    Glide.with(getApplicationContext()).load(model.getImage()).into(binding.icon3);
                } else if (package4.equals(model.getPackageName())){
                    Glide.with(getApplicationContext()).load(model.getImage()).into(binding.icon4);
                }
            }
        }
        initializeDock();
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