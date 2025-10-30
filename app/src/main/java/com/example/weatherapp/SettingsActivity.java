package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private SharedPreferences sharedPreferences;
    private static final String THEME_KEY = "theme_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        themeRadioGroup = findViewById(R.id.theme_radio_group);
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);

        loadTheme();

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.light_mode_radio) {
                saveTheme(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.dark_mode_radio) {
                saveTheme(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == R.id.system_default_radio) {
                saveTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
    }

    private void loadTheme() {
        int savedTheme = sharedPreferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (savedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            themeRadioGroup.check(R.id.light_mode_radio);
        } else if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            themeRadioGroup.check(R.id.dark_mode_radio);
        } else {
            themeRadioGroup.check(R.id.system_default_radio);
        }
    }

    private void saveTheme(int theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME_KEY, theme);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}