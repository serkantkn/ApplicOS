package com.serkantken.applicos.launcher.fragments;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.serkantken.applicos.R;
import com.serkantken.applicos.Utils;
import com.serkantken.applicos.databinding.FragmentAppListBinding;
import com.serkantken.applicos.launcher.AppClickListener;
import com.serkantken.applicos.launcher.MainActivity;
import com.serkantken.applicos.launcher.adapters.AppAdapter;
import com.serkantken.applicos.models.AppModel;
import com.serkantken.applicos.notes.NotesMainActivity;
import com.serkantken.applicos.settings.database.SettingsDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppListFragment extends Fragment implements PopupMenu.OnMenuItemClickListener
{
    private FragmentAppListBinding binding;
    private List<AppModel> appList;
    private AppAdapter adapter;
    private AppModel selectedModel;
    private SettingsDatabase database;

    public AppListFragment(List<AppModel> appList) {
        this.appList = appList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        requireActivity().setTheme(R.style.Theme_ApplicOS);

        database = SettingsDatabase.getInstance(requireContext());

        //appList = getInstalledApps();
        appList.sort(Comparator.comparing(AppModel::getName));
        binding.drawerGrid.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new AppAdapter(requireContext(), appList, appClickListener);
        binding.drawerGrid.setAdapter(adapter);

        Utils utils = new Utils(requireActivity(), requireContext());
        utils.blur(binding.backblur, 5f, false);

        return binding.getRoot();
    }

    private final AppClickListener appClickListener = new AppClickListener() {
        @Override
        public void onClick(AppModel model) {
            Intent launchAppIntent = requireContext().getPackageManager().getLaunchIntentForPackage(model.getPackageName());
            if (launchAppIntent != null)
            {
                requireContext().startActivity(launchAppIntent);
            }
        }

        @Override
        public void onLongClick(AppModel model, ConstraintLayout container) {
            selectedModel = model;
            showPopup(container);
        }
    };

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

    private void showPopup(ConstraintLayout container)
    {
        PopupMenu popupMenu = new PopupMenu(requireContext(), container);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.app_list_popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.pin_to_dock:
                database.editStringPreference("dockIcons", "dockIcon4", database.getStringPreference("dockIcons", "dockIcon3"));
                database.editStringPreference("dockIcons", "dockIcon3", database.getStringPreference("dockIcons", "dockIcon2"));
                database.editStringPreference("dockIcons", "dockIcon2", database.getStringPreference("dockIcons", "dockIcon1"));
                database.editStringPreference("dockIcons", "dockIcon1", selectedModel.getPackageName());
                MainActivity mainActivity = new MainActivity();
                mainActivity.setDockIcons(appList, selectedModel);
                return true;
            default:
                return false;
        }
    }
}