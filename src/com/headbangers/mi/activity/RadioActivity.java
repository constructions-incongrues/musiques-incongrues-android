package com.headbangers.mi.activity;

import java.io.IOException;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;

public class RadioActivity extends GuiceListActivity {

    protected static MediaPlayer mediaPlayer = new MediaPlayer();

    @Inject
    protected SharedPreferences prefs;
    
    @Inject
    private DataAccessService data;

    @InjectResource(R.string.radio_buffer)
    private String defaultTextForBuffer;

    private DataPage page;
    private MediaPlayer.OnPreparedListener mediaPlayerAsyncLauncher = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            buffer.setText("Yeaaaaaah :D");
            Toast.makeText(RadioActivity.this, "Le morceau démarre !", 1000)
                    .show();
        }
    };

    @InjectView(R.id.barStop)
    private ImageButton barStop;
    @InjectView(R.id.barRefresh)
    private ImageButton barRefresh;

    @InjectView(R.id.radioBufferText)
    private TextView buffer; // utiliser ce champ comme indicateur des actions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);

        //prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // TODO : chercher les morceaux dans une base : seul le bouton refresh
        // va chercher sur le net
        page = data.retrieveLastNLinks(10);
        setListAdapter(new RadioAdapter(this));

        barStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buffer.setText(defaultTextForBuffer);
                stopSong(false); // TODO true pour faire un release des
                                 // ressources
            }
        });

        barRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                page = data.retrieveLastNLinks(10);
                refreshList();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MILinkData data = page.getData().get(position);
        playSong(data.getUrl());
    }

    class RadioAdapter extends ArrayAdapter<MILinkData> {

        private Activity context;

        public RadioAdapter(Activity context) {
            super(context, R.layout.one_song, page.getData());
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.one_song, null);
            }

            MILinkData data = page.getData().get(position);

            TextView songTitle = (TextView) row.findViewById(R.id.songTitle);
            songTitle.setText(data.getTitle());
            TextView songContributor = (TextView) row
                    .findViewById(R.id.songContributor);
            songContributor.setText(data.getContributorName());

            return row;
        }

    }

    protected void playSong(String songUrl) {
        stopSong(false);
        mediaPlayer.setOnPreparedListener(mediaPlayerAsyncLauncher);
        if (tryToLoadSongInPlayer(songUrl, true)) { // true pour un workaround a
            // propos d'un bug de chargement
            // de resource sur MediaPlayer
            mediaPlayer.prepareAsync();
            Toast.makeText(this, "En cours de chargement ...", 1000).show();
            buffer.setText("En cours de chargement, patientez...");
        } 
    }

    protected void stopSong(boolean itsOver) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            if (itsOver) {
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean tryToLoadSongInPlayer(String songUrl, boolean reTryIfFail) {
        try {
            mediaPlayer.setDataSource(songUrl);
            return true;
        } catch (IllegalArgumentException e) {
            Toast.makeText(
                    this,
                    "Désolé, je n'ai pas pu lire ce morceau : problème dans le code",
                    1000).show();
        } catch (IllegalStateException e) {
            if (reTryIfFail) {
                return tryToLoadSongInPlayer(songUrl, false);
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(
                    this,
                    "Désolé, je n'ai pas pu lire ce morceau : la chanson n'existe peut-être plus ...",
                    1000).show();
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Création d'un MenuInflater qui va permettre d'instancier un Menu XML
        // en un objet Menu
        MenuInflater inflater = getMenuInflater();
        // Instanciation du menu XML spécifier en un objet Menu
        inflater.inflate(R.menu.radio_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuRadioHasard:
            page = data.retrieveShuffledNLinks(prefs.getInt("radioPreferences.nbSongs", 10));
            refreshList();
            return true;
        case R.id.menuRadioPreferences:
            Intent intent = new Intent(getBaseContext(), RadioPreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void refreshList() {
        ((RadioAdapter) getListAdapter()).notifyDataSetChanged();
    }
}
