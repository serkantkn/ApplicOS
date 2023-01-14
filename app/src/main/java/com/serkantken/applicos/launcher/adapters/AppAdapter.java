package com.serkantken.applicos.launcher.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serkantken.applicos.databinding.AppIconItemBinding;
import com.serkantken.applicos.launcher.AppClickListener;
import com.serkantken.applicos.models.AppModel;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder>
{
    private final Context context;
    private final List<AppModel> appList;
    private AppClickListener appClickListener;

    public AppAdapter(Context context, List<AppModel> appList, AppClickListener appClickListener)
    {
        this.context = context;
        this.appList = appList;
        this.appClickListener = appClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(AppIconItemBinding.inflate(LayoutInflater.from(context),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.binding.appIcon.setImageDrawable(appList.get(position).getImage());
        holder.binding.appTitle.setText(appList.get(position).getName());

        holder.binding.appIconContainer.setOnClickListener(view1 -> {
            appClickListener.onClick(appList.get(position));
        });

        holder.binding.appIconContainer.setOnLongClickListener(v -> {
            appClickListener.onLongClick(appList.get(position), holder.binding.appIconContainer);
            return true;
        });
    }

    @Override
    public int getItemCount()
    {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        AppIconItemBinding binding;

        public ViewHolder(@NonNull AppIconItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}


/*BaseAdapter
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
}*/
