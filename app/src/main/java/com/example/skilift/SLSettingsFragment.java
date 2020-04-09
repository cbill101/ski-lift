package com.example.skilift;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SLSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
    }
}
