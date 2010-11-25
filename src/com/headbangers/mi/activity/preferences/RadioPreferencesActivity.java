package com.headbangers.mi.activity.preferences;

import roboguice.activity.GuicePreferenceActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.headbangers.mi.R;

public class RadioPreferencesActivity extends GuicePreferenceActivity {

    private static final int DIALOG_NUMBER = 0;

    private SharedPreferences preferences;

    // DIALOG Input Number
    private EditText dialogNumberInput;
    private Button dialogNumberPlus;
    private Button dialogNumberMoins;
    private Button dialogNumberOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = this.getApplicationContext().getSharedPreferences(
                "musiques-incongrues-android", Activity.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.radio_preferences);

        Preference customPref = (Preference) findPreference("radioPreferences.nbSongs");
        customPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {

                        showDialog(DIALOG_NUMBER);
                        return true;
                    }

                });

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_NUMBER:
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.dialog_number);
            dialog.setTitle("Combien ?");

            dialogNumberInput = (EditText) dialog
                    .findViewById(R.id.dialogNumberInput);
            dialogNumberInput.setText(""
                    + preferences.getInt("radioPreferences.nbSongs", 10));

            dialogNumberPlus = (Button) dialog
                    .findViewById(R.id.dialogNumberPlus);
            dialogNumberPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    addToNumberText(1);
                }
            });

            dialogNumberMoins = (Button) dialog
                    .findViewById(R.id.dialogNumberMoins);
            dialogNumberMoins.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    addToNumberText(-1);
                }
            });

            dialogNumberOk = (Button) dialog.findViewById(R.id.dialogNumberOk);
            dialogNumberOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    saveNumber();
                    dialog.dismiss();
                }
            });

            return dialog;
        }
        return null;
    }

    protected void saveNumber() {
        SharedPreferences customSharedPreference = getSharedPreferences(
                "musiques-incongrues-android", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = customSharedPreference.edit();

        String value = dialogNumberInput.getText().toString();
        int converted = 9;
        try {
            converted = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }

        editor.putInt("radioPreferences.nbSongs", converted);
        editor.commit();
    }

    protected void addToNumberText(int addIt) {
        String value = dialogNumberInput.getText().toString();
        int converted = 9;
        try {
            converted = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }

        converted += addIt;

        if (converted <= 0) {
            converted = 1;
        } else if (converted > 100) {
            converted = 100;
        }

        dialogNumberInput.setText("" + converted); // bourrin
    }

}
