package com.headbangers.mi.activity.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;

import com.headbangers.mi.R;

public class RadioPreferencesActivity extends GenericPreferences {

    @Override
    protected String giveMePreferenceNumberKey() {
        return "radioPreferences.nbSongs";
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        addPreferencesFromResource(R.xml.radio_preferences);

        Preference customPref = (Preference) findPreference(giveMePreferenceNumberKey());
        customPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {

                        showDialog(DIALOG_SELECT_NUMBER);
                        return true;
                    }

                });

    }

}
