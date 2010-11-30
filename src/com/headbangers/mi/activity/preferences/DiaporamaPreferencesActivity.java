package com.headbangers.mi.activity.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.headbangers.mi.R;

import roboguice.activity.GuicePreferenceActivity;

public class DiaporamaPreferencesActivity extends GuicePreferenceActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        addPreferencesFromResource(R.xml.diaporama_preferences);
    }

}
