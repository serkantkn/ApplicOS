package com.serkantken.applicos.launcher.fragments;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.fragment.app.Fragment;

import com.serkantken.applicos.databinding.FragmentAppListBinding;
import com.serkantken.applicos.launcher.adapters.AppAdapter;
import com.serkantken.applicos.models.AppModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class AppListFragment extends Fragment
{
    private FragmentAppListBinding binding;
    private List<AppModel> appList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAppListBinding.inflate(inflater, container, false);

        appList = getInstalledApps();
        appList.sort(Comparator.comparing(AppModel::getName));
        binding.drawerGrid.setAdapter(new AppAdapter(requireContext(), requireActivity(), appList));

        blur(binding.backblur, 5f, false);

        return binding.getRoot();
    }

    private List<AppModel> getInstalledApps()
    {
        List<AppModel> list = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> untreatedAppList = requireContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo app : untreatedAppList)
        {
            String appName = app.loadLabel(requireContext().getPackageManager()).toString();
            String appPackageName = app.activityInfo.packageName;
            Drawable appImage = app.activityInfo.loadIcon(requireContext().getPackageManager());
            AppModel appModel = new AppModel(appName, appPackageName, appImage);
            if (!list.contains(appModel))
            {
                list.add(appModel);
            }
        }
        return list;
    }

    public void blur(BlurView view, float radius, boolean isRounded) {
        View decorView = requireActivity().getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        view.setupWith(rootView, new RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
        if (isRounded) {
            view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            view.setClipToOutline(true);
        }
    }
}