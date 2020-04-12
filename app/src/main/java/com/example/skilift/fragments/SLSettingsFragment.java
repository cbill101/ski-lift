package com.example.skilift.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.skilift.R;

public class SLSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
