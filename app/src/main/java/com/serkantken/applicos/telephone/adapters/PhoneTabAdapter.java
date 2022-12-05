package com.serkantken.applicos.telephone.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.serkantken.applicos.telephone.fragments.CallLogFragment;
import com.serkantken.applicos.telephone.fragments.ContactsFragment;

public class PhoneTabAdapter  extends FragmentPagerAdapter
{
    private Context context;
    int totalTabs;

    public PhoneTabAdapter(@NonNull FragmentManager fm, Context context, int totalTabs)
    {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount()
    {
        return totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new CallLogFragment();
            case 1:
                return new ContactsFragment();
        }
        return null;
    }
}
