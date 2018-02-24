package com.ferfig.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //PreferenceManager.setDefaultValues(getContext(), R.xml.popularmovie_settings, false);
        addPreferencesFromResource(R.xml.popularmovie_settings);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (p instanceof ListPreference) {
                String val = sharedPreferences.getString(p.getKey(), "");
                setPrefSummary(p, val);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference currentPref =  findPreference(key);
        if (currentPref!=null){
            if (currentPref instanceof ListPreference){
                String desc = sharedPreferences.getString(currentPref.getKey(), "");
                setPrefSummary(currentPref, desc);
            }
        }
    }

    private void setPrefSummary(Preference preference, String value) {
        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference)preference;
            int preIdx = listPreference.findIndexOfValue(value);
            if (preIdx>=0){
                listPreference.setSummary(listPreference.getEntries()[preIdx]);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
