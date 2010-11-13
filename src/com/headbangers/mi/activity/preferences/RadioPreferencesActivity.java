package com.headbangers.mi.activity.preferences;

import roboguice.activity.GuicePreferenceActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.headbangers.mi.R;

public class RadioPreferencesActivity extends GuicePreferenceActivity {

    private static final int DIALOG_NUMBER = 0;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        addPreferencesFromResource(R.xml.radio_preferences);

        Preference customPref = (Preference) findPreference("radioPreferences.nbSongs");
        customPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {

                        showDialog(DIALOG_NUMBER);

                        SharedPreferences customSharedPreference = getSharedPreferences(
                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = customSharedPreference
                                .edit();
                        editor.putString("myCustomPref",
                                "The preference has been clicked");
                        editor.commit();
                        return true;
                    }

                });

    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case DIALOG_NUMBER:
            dialog = new Dialog(this);

            dialog.setContentView(R.layout.dialog_number);
            dialog.setTitle("Combien ?");

            EditText number = (EditText) dialog
                    .findViewById(R.id.dialogNumberInput);
            number.setText(""+preferences.getInt("radioPreferences.nbSongs", 10));

            break;
        }
        return dialog;
    }

}
