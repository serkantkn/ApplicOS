package com.serkantken.applicos.launcher.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.serkantken.applicos.launcher.fragments.AppListFragment;
import com.serkantken.applicos.launcher.fragments.WidgetScreenFragment;

public class LauncherPageAdapter extends FragmentStateAdapter
{
    private int totalTabs = 0;

    public LauncherPageAdapter(@NonNull FragmentActivity fragmentActivity, int totalTabs) {
        super(fragmentActivity);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new AppListFragment();
        }
        return new WidgetScreenFragment();
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
