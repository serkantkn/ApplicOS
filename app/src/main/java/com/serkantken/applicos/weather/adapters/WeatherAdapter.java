package com.serkantken.applicos.weather.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.serkantken.applicos.databinding.WeatherItemBinding;
import com.serkantken.applicos.models.WeatherModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>
{
    Context context;
    ArrayList<WeatherModel> weatherList;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherList)
    {
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
        holder.binding.temperature.setText(String.format("%s °C", weather.getTemperature()));
        Glide.with(context).load("https:".concat(weather.getIcon())).into(holder.binding.weatherIcon);
        holder.binding.windSpeed.setText(String.format("%s Km/h", weather.getWindSpeed()));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        try {
            Date time = input.parse(weather.getTime());
            holder.binding.time.setText(output.format(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        WeatherItemBinding binding;

        public ViewHolder(@NonNull WeatherItemBinding itemView)
        {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
