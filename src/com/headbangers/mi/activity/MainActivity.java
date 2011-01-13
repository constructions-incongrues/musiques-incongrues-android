package com.headbangers.mi.activity;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.headbangers.mi.R;
import com.headbangers.mi.tools.AudioPlayer;

public class MainActivity extends GuiceListActivity {

    private static final int MENU_RADIO = 0;
    private static final int MENU_DIAPORAMA = 1;
    private static final int MENU_DISCUSSIONS = 2;
    private static final int MENU_TELEVISION = 3;
    private static final int MENU_CESOIR = 4;
    private static final int MENU_AGENDA = 5;
    private static final int MENU_RELEASES = 6;
    private static final int MENU_MEMBRES = 7;
    private static final int MENU_CONTACTS = 8;

    @InjectResource(R.array.main_menu)
    protected String[] menus;
    @InjectResource(R.string.version)
    protected String version;
    @InjectResource(R.string.app_title)
    protected String appTitle;

    protected SharedPreferences prefs;
    
    protected static int[] icons = { R.drawable.grapp01, R.drawable.pin01,
            R.drawable.grapp02, R.drawable.pin02, R.drawable.grapp03,
            R.drawable.pin03, R.drawable.grapp04, R.drawable.pin04,
            R.drawable.grapp05, R.drawable.pin05, R.drawable.grapp06,
            R.drawable.pin06 };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setListAdapter(new MainMenuAdapter(this));
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());
        boolean firstRun = prefs.getBoolean("mi-"+version, true);
        if (firstRun){
            prefs.edit().putBoolean("mi-"+version, false).commit();
            // affichage du dialogue de bienvenue
            showWelcomeDialog ();
        }
    }

    private void showWelcomeDialog() {
        Dialog dialog = new Dialog(this, 0);
        
        dialog.setContentView(R.layout.welcome_dialog);
        dialog.setTitle(appTitle);       

        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MediaPlayer player = AudioPlayer.androidMediaPlayer;
        if (player.isPlaying()) {
            player.setOnCompletionListener(null);
            player.stop();
            player.release();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent gotoPage = null;

        switch (position) {
        case MENU_RADIO:
            gotoPage = new Intent(this, RadioActivity.class);
            break;
        case MENU_DIAPORAMA:
            gotoPage = new Intent(this, DiaporamaActivity.class);
            break;
        case MENU_AGENDA:
            gotoPage = new Intent(this, AgendaActivity.class);
            break;
        case MENU_CONTACTS:
            gotoPage = new Intent (this, ContactsActivity.class);
            break;
        case MENU_RELEASES:
            gotoPage = new Intent (this, ReleasesActivity.class);
            break;
        case MENU_CESOIR:
        case MENU_DISCUSSIONS:
        case MENU_MEMBRES:
            break;
        case MENU_TELEVISION:
            gotoPage = new Intent (this, TelevisionActivity.class);
            break;
        }

        if (gotoPage != null) {
            startActivity(gotoPage);
        }
    }

    class MainMenuAdapter extends ArrayAdapter<String> {

        private Activity context;

        public MainMenuAdapter(Activity context) {
            super(context, R.layout.main_menu_item, menus);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.main_menu_item, null);
            }

            TextView label = (TextView) row.findViewById(R.id.menuLabel);
            TextView disclaimer = (TextView) row
                    .findViewById(R.id.menuDisclaimer);
            ImageView icon = (ImageView) row.findViewById(R.id.menuIcon);

            String[] menuDefinition = menus[position].split("_");

            label.setText(menuDefinition[0]);
            disclaimer.setText(menuDefinition[1]);

            icon.setImageResource(icons[position]);

            return row;
        }
    }
}
