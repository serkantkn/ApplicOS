package com.serkantken.applicos.launcher.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.serkantken.applicos.launcher.fragments.AppListFragment;
import com.serkantken.applicos.launcher.fragments.WidgetScreenFragment;
import com.serkantken.applicos.models.AppModel;

import java.util.List;

public class LauncherPageAdapter extends FragmentStateAdapter
{
    private List<AppModel> installedApps;
    private int totalTabs;

    public LauncherPageAdapter(@NonNull FragmentActivity fragmentActivity, List<AppModel> installedApps, int totalTabs) {
        super(fragmentActivity);
        this.installedApps = installedApps;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new AppListFragment(installedApps);
        }
        return new WidgetScreenFragment();
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
