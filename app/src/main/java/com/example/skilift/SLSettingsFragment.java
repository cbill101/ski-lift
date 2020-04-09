package com.example.skilift;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SLSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
