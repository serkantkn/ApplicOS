package com.serkantken.applicos.clockalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityClockMainBinding;

public class ClockMainActivity extends AppCompatActivity {
    private ActivityClockMainBinding binding;
    private ClockAdapter clockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClockMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(view -> onBackPressed());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getResources().getString(R.string.clock)));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(""));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(""));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager));
        int[] tabicons = {R.drawable.ic_clock, R.drawable.ic_alarm, R.drawable.ic_timer};
        binding.tabLayout.getTabAt(1).setIcon(tabicons[1]);
        binding.tabLayout.getTabAt(2).setIcon(tabicons[2]);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                {
                    tab.setText(getResources().getString(R.string.clock));
                    tab.setIcon(null);
                    tab.view.performClick();
                }
                else if (tab.getPosition() == 1)
                {
                    tab.setText(getResources().getString(R.string.alarm));
                    tab.setIcon(null);
                    tab.view.performClick();
                }
                else if (tab.getPosition() == 2)
                {
                    tab.setText(getResources().getString(R.string.timer));
                    tab.setIcon(null);
                    tab.view.performClick();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                {
                    tab.setText("");
                    tab.setIcon(tabicons[0]);
                }
                else if (tab.getPosition() == 1)
                {
                    tab.setText("");
                    tab.setIcon(tabicons[1]);
                }
                else if (tab.getPosition() == 2)
                {
                    tab.setText("");
                    tab.setIcon(tabicons[2]);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        clockAdapter = new ClockAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(clockAdapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
    }
}