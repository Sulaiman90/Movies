package com.innovae.movies.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.innovae.movies.R;
import com.innovae.movies.dialog.SortDialogFragment;

import java.util.List;

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String TAG = PreferencesFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.preferences);

        Context context = getActivity();

        context.setTheme(R.style.settingsTheme);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       // Log.d(TAG,"onSharedPreferenceChanged " );
        if(key.matches(getString(R.string.pref_night_mode_key))){
            setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        //Log.d(TAG,"setNightMode");
        AppCompatDelegate.setDefaultNightMode(nightMode);
        getActivity().recreate();
    }
}
