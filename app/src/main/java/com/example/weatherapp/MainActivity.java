package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    // UI elements
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private EditText etCity;
    private ImageButton btnGetWeather;
    private TextView tvCurrentCity, tvDateTime, tvTemperature, tvCondition;
    private TextView tvFeelsLike, tvMinMaxTemp, tvHumidity, tvWind, tvPressure, tvVisibility, tvClouds, tvSunrise, tvSunset;
    private ImageView ivWeatherIcon;
    private TextView tvWeatherResult;

    private WeatherService weatherService;

    private static final DecimalFormat tempFormat = new DecimalFormat("0");

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "fec3847282fb7a9c2f04d2cbf8fd0c24";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply the saved theme before setting the content view
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        int savedTheme = sharedPreferences.getInt("theme_key", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);

        setContentView(R.layout.activity_main);

        initializeUI();
        setupRetrofit();
        setupListeners();
        setupDrawer();
        resetWeatherUI(null); // Set initial placeholder text
    }

    private void initializeUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        tvCurrentCity = findViewById(R.id.tvCurrentCity);
        tvDateTime = findViewById(R.id.tvDateTime);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvMinMaxTemp = findViewById(R.id.tvMinMaxTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvPressure = findViewById(R.id.tvPressure);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvClouds = findViewById(R.id.tvClouds);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvWeatherResult = findViewById(R.id.tvWeatherResult);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherService.class);
    }

    private void setupListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        btnGetWeather.setOnClickListener(v -> fetchWeatherForCity());
        etCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                fetchWeatherForCity();
                return true;
            }
            return false;
        });
    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void fetchWeatherForCity() {
        String city = etCity.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show();
            return;
        }
        fetchWeather(city);
    }

    private void fetchWeather(String city) {
        tvWeatherResult.setText("");
        tvCondition.setText("Fetching data...");

        Call<WeatherResponse> call = weatherService.getWeather(city, API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWeatherUI(response.body());
                } else {
                    resetWeatherUI("City not found or data unavailable.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
                resetWeatherUI("Error fetching data: " + t.getMessage());
            }
        });
    }

    private void updateWeatherUI(WeatherResponse weather) {
        if (weather.getList() != null && !weather.getList().isEmpty()) {
            WeatherResponse.ForecastItem forecastItem = weather.getList().get(0);
            WeatherResponse.City city = weather.getCity();

            tvCurrentCity.setText(city.getName());
            tvDateTime.setText(getCurrentDateTime());

            String temp = tempFormat.format(forecastItem.getMain().getTemp()) + "°C";
            tvTemperature.setText(temp);

            String description = forecastItem.getWeather().get(0).getDescription();
            String capitalizedCondition = description.substring(0, 1).toUpperCase(Locale.ROOT) + description.substring(1);
            tvCondition.setText(capitalizedCondition);

            String feelsLike = tempFormat.format(forecastItem.getMain().getFeelsLike()) + "°C";
            tvFeelsLike.setText(feelsLike);

            String minMaxTemp = tempFormat.format(forecastItem.getMain().getTempMin()) + "°/" + tempFormat.format(forecastItem.getMain().getTempMax()) + "°";
            tvMinMaxTemp.setText(minMaxTemp);

            tvHumidity.setText(forecastItem.getMain().getHumidity() + "%");
            tvWind.setText(String.format(Locale.ROOT, "%.1f km/h", forecastItem.getWind().getSpeed()));
            tvPressure.setText(forecastItem.getMain().getPressure() + " hPa");
            tvVisibility.setText(String.format(Locale.ROOT, "%.1f km", forecastItem.getVisibility() / 1000.0));
            tvClouds.setText(forecastItem.getClouds().getAll() + "%");
            tvSunrise.setText(formatTime(city.getSunrise()));
            tvSunset.setText(formatTime(city.getSunset()));

            String iconUrl = "https://openweathermap.org/img/wn/" + forecastItem.getWeather().get(0).getIcon() + ".png";
            Picasso.get().load(iconUrl).into(ivWeatherIcon);
        } else {
            resetWeatherUI("No forecast data available.");
        }
    }

    private void resetWeatherUI(String message) {
        tvCurrentCity.setText(R.string.default_city);
        tvDateTime.setText(R.string.default_datetime);
        tvTemperature.setText(R.string.default_temp);
        tvCondition.setText(R.string.default_condition);
        tvFeelsLike.setText(R.string.default_temp);
        tvMinMaxTemp.setText(R.string.default_min_max_temp);
        tvHumidity.setText(R.string.default_humidity);
        tvWind.setText(R.string.default_wind_speed);
        tvPressure.setText(R.string.default_pressure);
        tvVisibility.setText(R.string.default_visibility);
        tvClouds.setText(R.string.default_clouds);
        tvSunrise.setText(R.string.default_sunrise);
        tvSunset.setText(R.string.default_sunset);

        if (message != null) {
            tvWeatherResult.setText(message);
            Log.e(TAG, "UI Reset with Error: " + message);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            // You are already on the home screen
        } else if (itemId == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_logout) {
            // Handle the logout action, for example, navigate to a login screen
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, h:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String formatTime(long unixTimestamp) {
        Date date = new Date(unixTimestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
