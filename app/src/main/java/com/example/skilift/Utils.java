package com.example.skilift;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public class Utils {
    public static void toggleTheme(String choice) {
        switch(choice) {
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                int sdk_int = Build.VERSION.SDK_INT;
                if(sdk_int >= Build.VERSION_CODES.P)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }

    public static void loadThemePreference(SharedPreferences sp) {
        String choice = sp.getString("key_theme_choice", "0");
        toggleTheme(choice);
    }
}
