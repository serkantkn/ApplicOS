package com.serkantken.applicos.clockalarm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.serkantken.applicos.clockalarm.fragments.AlarmFragment;
import com.serkantken.applicos.clockalarm.fragments.ClockFragment;
import com.serkantken.applicos.clockalarm.fragments.TimerFragment;

public class ClockAdapter  extends FragmentPagerAdapter
{
    private Context context;
    int totalTabs;

    public ClockAdapter(@NonNull FragmentManager fm, Context context, int totalTabs)
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
                return new ClockFragment();
            case 1:
                return new AlarmFragment();
            case 2:
                return new TimerFragment();
        }
        return null;
    }
}


