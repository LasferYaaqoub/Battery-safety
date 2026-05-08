package com.example.batterysafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.example.batterysafety.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    private int currentTheme = 0; // To keep track of the current theme
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
     String[] themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("mode", Context.MODE_PRIVATE);
        currentTheme = sharedPreferences.getInt("theme",0);
        getTheme(currentTheme);

        binding.tilTheme.setBackground(getResources().getDrawable(R.drawable.ripple_effect,getTheme()));

        binding.tilTheme.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                changeTheme();

            }
        });
    }

    private void changeTheme() {
        currentTheme = (currentTheme + 1) % getResources().getStringArray(R.array.themes).length;
        editor = sharedPreferences.edit();

        if (currentTheme == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            editor.putInt("theme",currentTheme);
            binding.tvTheme.setText("System");
        } else if (currentTheme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("theme",currentTheme);
            binding.tvTheme.setText("Dark");
        } else if (currentTheme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("theme",currentTheme);
            binding.tvTheme.setText("Light");
        }

        editor.apply();
//        recreate(); // Recreate the activity to apply the new theme
    }
    private void getTheme(int currentTheme){
        if (currentTheme == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            binding.tvTheme.setText("System");
        } else if (currentTheme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            binding.tvTheme.setText("Dark");
        } else if (currentTheme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding.tvTheme.setText("Light");
        }

    }
}