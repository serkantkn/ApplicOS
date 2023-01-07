package com.serkantken.applicos.weather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.serkantken.applicos.Utils;
import com.serkantken.applicos.databinding.WeatherItemBinding;
import com.serkantken.applicos.models.WeatherModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>
{
    Utils utils;
    Context context;
    ArrayList<WeatherModel> weatherList;

    public WeatherAdapter(Utils utils, Context context, ArrayList<WeatherModel> weatherList)
    {
        this.utils = utils;
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(WeatherItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        WeatherModel weather = weatherList.get(position);
        SimpleDateFormat dayMonth = new SimpleDateFormat("dd MMMM");
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE");
        try {
            Date date = new Date(weather.getTime());

            String dayMonthString = dayMonth.format(date);
            holder.binding.dayMonth.setText(dayMonthString);

            String dayOfWeekString = dayOfWeek.format(date);
            holder.binding.dayOfWeek.setText(dayOfWeekString);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        holder.binding.maxTemperature.setText(String.valueOf(weather.getMax_temp()));
        holder.binding.minTemperature.setText(String.valueOf(weather.getMin_temp()));

        int iconNo = weather.getDay_icon();
        String eleman1 = String.valueOf(iconNo);
        String iconDay;
        if (eleman1.length() == 2)
        {
            iconDay = "https://developer.accuweather.com/sites/default/files/"+eleman1+"-s.png";
        }
        else
        {
            iconDay = "https://developer.accuweather.com/sites/default/files/0"+eleman1+"-s.png";
        }
        Glide.with(context).load(iconDay).into(holder.binding.weatherIconDay);

        int iconNoN = weather.getNight_icon();
        String eleman2 = String.valueOf(iconNo);
        String iconNight;
        if (eleman2.length() == 2)
        {
            iconNight = "https://developer.accuweather.com/sites/default/files/"+eleman2+"-s.png";
        }
        else
        {
            iconNight = "https://developer.accuweather.com/sites/default/files/0"+eleman2+"-s.png";
        }
        Glide.with(context).load(iconNight).into(holder.binding.weatherIconNight);

        utils.blur(holder.binding.weatherItemContainer, 10f, true);
    }

    @Override
    public int getItemCount()
    {
        return weatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        WeatherItemBinding binding;

        public ViewHolder(@NonNull WeatherItemBinding itemView)
        {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
