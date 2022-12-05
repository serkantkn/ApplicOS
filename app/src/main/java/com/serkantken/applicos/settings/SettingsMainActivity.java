package com.serkantken.applicos.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.serkantken.applicos.databinding.ActivitySettingsMainBinding;

public class SettingsMainActivity extends AppCompatActivity
{
    private ActivitySettingsMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}