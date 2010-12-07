package com.headbangers.mi.activity.preferences;

import com.headbangers.mi.R;

import android.os.Bundle;

public class ReleasesPreferencesActivity extends GenericPreferences{

    @Override
    protected String giveMePreferenceNumberKey() {
        return null;
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.releases_preferences);
    }
    
}
