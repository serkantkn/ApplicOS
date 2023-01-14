package com.serkantken.applicos.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityMessagingMainBinding;

public class MessagingMainActivity extends AppCompatActivity {
    private ActivityMessagingMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagingMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}