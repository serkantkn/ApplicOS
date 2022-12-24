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
import com.serkantken.applicos.telephone.database.ContactsDB;
import com.serkantken.applicos.weather.WeatherMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private Location location;
    private LocationManager locationManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentWidgetScreenBinding.inflate(inflater, container, false);

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        blurViews(binding.weatherContainer, binding.mediaContainer, binding.notesContainer, binding.contactsContainer);

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
        getFavouriteContacts();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            setWeatherWidget(Constants.apiKey, location.getLongitude(), location.getLatitude(), Constants.lang);
        }

        binding.notesTitle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotesMainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.notesContainer, Objects.requireNonNull(ViewCompat.getTransitionName(binding.notesContainer)));
            startActivity(intent, optionsCompat.toBundle());
        });

        binding.iconAdd.setOnClickListener(v -> addNote());

        binding.weatherContainer.setOnClickListener(v -> {
            if (binding.textWarning.getVisibility() == View.GONE)
            {
                Intent intent = new Intent(requireContext(), WeatherMainActivity.class);
                startActivity(intent);
            }
            else
            {
                setWeatherWidget(Constants.apiKey, location.getLongitude(), location.getLatitude(), Constants.lang);
            }
        });
        return binding.getRoot();
    }

    private void blurViews(BlurView... blurView) {
        for (BlurView view : blurView) {
            blur(view, 10f, true);
        }
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

    private void setWeatherWidget(String apiKey, double longitude, double latitude, String lang)
    {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        else
        {
            String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+apiKey+"&q="+latitude+"%2C%20"+longitude+"&language="+lang;

            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    String key = response.getString("Key");
                    binding.weatherTitle.setText(response.getString("LocalizedName"));
                    getWeatherInfo(apiKey, key, lang);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }, error -> {
                binding.textWarning.setText(error.getMessage());
                binding.textWarning.setVisibility(View.VISIBLE);
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void getWeatherInfo (String apiKey, String cityName, String lang)
    {
        String url = "https://dataservice.accuweather.com/forecasts/v1/hourly/1hour/"+cityName+"?apikey="+apiKey+"&language="+lang+"&details=true&metric=true";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try
            {
                JSONObject jsonObject = response.getJSONObject(0);
                binding.condition.setText(jsonObject.getString("IconPhrase"));
                binding.temperature.setText(String.format("%s °C", jsonObject.getJSONObject("Temperature").getDouble("Value")));
                int iconNo = jsonObject.getInt("WeatherIcon");
                String eleman = String.valueOf(iconNo);
                String icon = "";
                if (eleman.length() == 2)
                {
                    icon = "https://developer.accuweather.com/sites/default/files/"+eleman+"-s.png";
                }
                else
                {
                    icon = "https://developer.accuweather.com/sites/default/files/0"+eleman+"-s.png";
                }
                Glide.with(this).load(icon).into(binding.iconForecast);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> {
            //Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(jsonObjectRequest);
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
        getFavouriteContacts();
        //setWeatherWidget(Constants.apiKey, location.getLongitude(), location.getLatitude(), Constants.lang);
    }
}