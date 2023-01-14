package com.serkantken.applicos.launcher.fragments;

import static android.content.Context.STATUS_BAR_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.serkantken.applicos.Constants;
import com.serkantken.applicos.R;
import com.serkantken.applicos.Utils;
import com.serkantken.applicos.clockalarm.ClockMainActivity;
import com.serkantken.applicos.databinding.FragmentWidgetScreenBinding;
import com.serkantken.applicos.launcher.MainActivity;
import com.serkantken.applicos.launcher.adapters.FavouritesAdapter;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.models.NotesModel;
import com.serkantken.applicos.notes.NotesClickListener;
import com.serkantken.applicos.notes.NotesMainActivity;
import com.serkantken.applicos.notes.adapters.NotesListAdapter;
import com.serkantken.applicos.notes.database.RoomDB;
import com.serkantken.applicos.settings.database.SettingsDatabase;
import com.serkantken.applicos.telephone.database.ContactsDB;
import com.serkantken.applicos.weather.WeatherMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class WidgetScreenFragment extends Fragment implements NotesClickListener
{
    private FragmentWidgetScreenBinding binding;
    private RoomDB notesDatabase;
    private ContactsDB contactsDatabase;
    private List<NotesModel> notes = new ArrayList<>();
    private List<ContactModel> contacts = new ArrayList<>();
    private SettingsDatabase settingsDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentWidgetScreenBinding.inflate(inflater, container, false);
        requireActivity().setTheme(R.style.Theme_ApplicOS);

        settingsDatabase = SettingsDatabase.getInstance(requireContext());
        Utils utils = new Utils(requireActivity(), requireContext());
        utils.blurMultipleViews(10f, true, binding.weatherContainer, binding.calendarContainer, binding.clockContainer, binding.notesContainer, binding.lastAppsContainer);

        //SwipeRefreshLayout yükleme diskini kaldırma
        try {
            Field f = binding.notifGesture.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView imageView = (ImageView) f.get(binding.notifGesture);
            assert imageView != null;
            imageView.setAlpha(0.0f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        binding.notifGesture.setOnRefreshListener(this::showNotifications);

        loadNotesWidget();
        //getFavouriteContacts();
        setWeatherWidget();
        setCalendarWidget();

        binding.notesTitle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotesMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.notesContainer, Objects.requireNonNull(ViewCompat.getTransitionName(binding.notesContainer)));
            startActivity(intent, optionsCompat.toBundle());
        });

        binding.iconAdd.setOnClickListener(v -> addNote());

        binding.weatherContainer.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WeatherMainActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

        return binding.getRoot();
    }

    private void showNotifications()
    {
        binding.notifGesture.setRefreshing(false);
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            @SuppressLint("WrongConstant") Object service = requireContext().getSystemService(STATUS_BAR_SERVICE);
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel");
            expand.invoke(service);
        }
        catch (Exception e) {
            Toast.makeText(requireContext(), "Expansion Not Working. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotesWidget() {
        notesDatabase = RoomDB.getInstance(requireContext());
        notes = notesDatabase.mainDAO().getAll();
        List<NotesModel> pinnedNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).isPinned()) {
                pinnedNotes.add(notes.get(i));
            }
        }
        if (pinnedNotes.size() > 0) {
            binding.notesContainer.setVisibility(View.VISIBLE);
            binding.desktopNoteRV.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
            binding.desktopNoteRV.setAdapter(new NotesListAdapter(requireContext(), pinnedNotes, this));
        } else {
            binding.notesContainer.setVisibility(View.GONE);
        }
    }

    private void setCalendarWidget()
    {
        long currentDate = new Date().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("EEEE");

        binding.tvDay.setText(dayFormat.format(currentDate));
        binding.tvMonth.setText(monthFormat.format(currentDate));
        binding.tvDayOfMonth.setText(dayOfMonthFormat.format(currentDate));
    }

    private void addNote() {
        Intent intent = new Intent(requireContext(), NotesMainActivity.class);
        intent.putExtra("requestCode", "10");
        startActivity(intent);
    }

    @Override
    public void onClick(NotesModel notes) {
        Intent intent = new Intent(requireContext(), NotesMainActivity.class);
        intent.putExtra("requestCode", "11");
        intent.putExtra("note", notes);
        startActivity(intent);
    }

    @Override
    public void onLongClick(NotesModel notes, CardView cardView) {

    }

    private void getFavouriteContacts() {
        contactsDatabase = ContactsDB.getInstance(requireContext());
        contacts = contactsDatabase.MainDao().getAll();
        if (contacts.size() == 0) {
            binding.contactsContainer.setVisibility(View.GONE);
        } else {
            binding.contactsContainer.setVisibility(View.VISIBLE);
            binding.desktopContactRV.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
            binding.desktopContactRV.setAdapter(new FavouritesAdapter(requireContext(), contacts));
        }
    }

    private void setWeatherWidget()
    {
        if (!Objects.equals(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_temperature), "null"))
        {
            binding.weatherTitle.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_cityName));
            binding.condition.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_condition));
            binding.condition.setSelected(true);
            binding.temperature.setText(settingsDatabase.getStringPreference(Constants.weatherPrefName, Constants.weather_temperature));
            //Glide.with(this).load(AppCompatResources.getDrawable(requireContext(), settingsDatabase.getIntegerPreference(Constants.weatherPrefName, Constants.weather_icon))).into(binding.iconForecast);
            binding.iconForecast.setAnimation(settingsDatabase.getIntegerPreference(Constants.weatherPrefName, Constants.weather_icon));
            binding.iconForecast.playAnimation();
            binding.textWarning.setVisibility(View.GONE);
        }
        else
        {
            binding.weatherTitle.setText(getString(R.string.weather));
            binding.textWarning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(requireContext(), "Permission is required for use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotesWidget();
        //getFavouriteContacts();
        setWeatherWidget();
        setCalendarWidget();
    }
}