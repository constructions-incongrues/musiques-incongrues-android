package com.headbangers.mi.activity.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;

import com.headbangers.mi.R;
import com.headbangers.mi.constant.PreferencesKeys;

public class DiaporamaPreferencesActivity extends GenericPreferences {

    @Override
    protected String giveMePreferenceNumberKey() {
        return PreferencesKeys.diaporamaNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        addPreferencesFromResource(R.xml.diaporama_preferences);

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
