package com.serkantken.applicos.launcher.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.serkantken.applicos.databinding.AppIconItemBinding;
import com.serkantken.applicos.models.AppModel;

import java.util.List;

public class AppAdapter extends BaseAdapter
{
    private final Context context;
    private final List<AppModel> appList;

    public AppAdapter(Context context, List<AppModel> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int i) {
        return appList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppIconItemBinding binding = AppIconItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        binding.appIcon.setImageDrawable(appList.get(i).getImage());
        binding.appTitle.setText(appList.get(i).getName());

        binding.appIconContainer.setOnClickListener(view1 -> {
            Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(appList.get(i).getPackageName());
            if (launchAppIntent != null)
            {
                context.startActivity(launchAppIntent);
            }
        });

        return binding.getRoot();
    }
}
