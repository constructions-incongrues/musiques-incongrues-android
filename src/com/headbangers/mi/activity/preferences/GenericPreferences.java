package com.headbangers.mi.activity.preferences;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.headbangers.mi.R;

import roboguice.activity.GuicePreferenceActivity;

public abstract class GenericPreferences extends GuicePreferenceActivity {

    protected static final int DIALOG_SELECT_NUMBER = 0;

    protected SharedPreferences preferences;

    // DIALOG Input Number
    private EditText dialogNumberInput;
    private Button dialogNumberPlus;
    private Button dialogNumberMoins;
    private Button dialogNumberOk;

    protected abstract String giveMePreferenceNumberKey ();
    protected Integer giveMePreferenceNumberValue (){
        return preferences.getInt(giveMePreferenceNumberKey(), 10);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SELECT_NUMBER:
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.dialog_number);
            dialog.setTitle("Combien ?");

            dialogNumberInput = (EditText) dialog
                    .findViewById(R.id.dialogNumberInput);
            dialogNumberInput.setText( giveMePreferenceNumberValue().toString() );

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
        SharedPreferences.Editor editor = preferences.edit();

        String value = dialogNumberInput.getText().toString();
        int converted = 9;
        try {
            converted = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }

        editor.putInt(giveMePreferenceNumberKey(), converted);
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
