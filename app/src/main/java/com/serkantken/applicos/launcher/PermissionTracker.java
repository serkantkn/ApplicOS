package com.serkantken.applicos.launcher;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionTracker
{
    private Activity activity;
    private Context context;

    public PermissionTracker(Activity activity)
    {
        this.activity = activity;
    }

    public boolean CheckPermission(String permission)
    {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestStoragePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            try
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
            }
            catch (Exception e)
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}
