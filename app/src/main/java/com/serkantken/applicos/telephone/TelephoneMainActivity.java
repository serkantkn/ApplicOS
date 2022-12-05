package com.serkantken.applicos.telephone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityTelephoneMainBinding;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.telephone.adapters.PhoneTabAdapter;
import com.serkantken.applicos.telephone.database.ContactsDB;

import java.util.List;
import java.util.Objects;

public class TelephoneMainActivity extends AppCompatActivity{
    private ActivityTelephoneMainBinding binding;
    private PhoneTabAdapter phoneTabAdapter;
    private ContactModel selectedContact;
    ContactsDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelephoneMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(view -> onBackPressed());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Calls"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(""));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager));
        int[] tabicons = {R.drawable.ic_phone, R.drawable.ic_person};
        binding.tabLayout.getTabAt(1).setIcon(tabicons[1]);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                {
                    tab.setText("Calls");
                    tab.setIcon(null);
                    tab.view.performClick();
                }
                else if (tab.getPosition() == 1)
                {
                    tab.setText("Contacts");
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
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        phoneTabAdapter = new PhoneTabAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(phoneTabAdapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
    }
}