package com.headbangers.mi.activity;

import java.io.IOException;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.RadioPreferencesActivity;
import com.headbangers.mi.activity.thread.ProgressBarThread;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.Segment;

public class RadioActivity extends GuiceListActivity {
    private static String TAG = "RadioActivity";    
    
    protected static MediaPlayer mediaPlayer = new MediaPlayer();

    @Inject
    protected SharedPreferences prefs;

    @Inject
    private DataAccessService data;

    @Inject
    private HttpService http;

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

            // lancement du thread d'update de la barre
            new Thread(new ProgressBarThread(progressBar, mediaPlayer)).start();
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mediaPlayerAsyncBuffering = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            progressBar.setSecondaryProgress(percent);
        }
    };

    @InjectView(R.id.barStop)
    private ImageButton barStop;

    @InjectView(R.id.barRefresh)
    private ImageButton barRefresh;

    @InjectView(R.id.radioBufferText)
    private TextView buffer; // utiliser ce champ comme indicateur des actions

    @InjectView(R.id.streamBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);

        // prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // TODO : chercher les morceaux dans une base : seul le bouton refresh
        // va chercher sur le net
        page = data.retrieveLastNLinks(Segment.MP3, 10);
        setListAdapter(new RadioAdapter(this));
        registerForContextMenu(getListView());

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
                page = data.retrieveLastNLinks(Segment.MP3, 10);
                refreshList();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MILinkData data = page.findInList(position);
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

            MILinkData data = page.findInList(position);

            TextView songTitle = (TextView) row.findViewById(R.id.songTitle);
            songTitle.setText(data.getTitle());
            TextView songContributor = (TextView) row
                    .findViewById(R.id.songContributor);
            songContributor.setText(data.getContributorName() + " // "
                    + data.getContributionDate());

            return row;
        }

    }

    protected void playSong(String songUrl) {
        Log.d(TAG, "Tentative de lecture de "+songUrl);
        
        stopSong(false);
        
        mediaPlayer.setOnPreparedListener(mediaPlayerAsyncLauncher);
        mediaPlayer.setOnBufferingUpdateListener(mediaPlayerAsyncBuffering);
        
        tryToLoadSongInPlayer(songUrl, true);
    }

    protected void stopSong(boolean itsOver) {
        try {

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            progressBar.setSecondaryProgress(0);
            progressBar.setProgress(0);

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
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            
            mediaPlayer.setOnPreparedListener(mediaPlayerAsyncLauncher);
            mediaPlayer.setOnBufferingUpdateListener(mediaPlayerAsyncBuffering);
            
            mediaPlayer.prepareAsync();
            Toast.makeText(this, "En cours de chargement ...", 1000).show();
            buffer.setText("En cours de chargement, patientez...");
            
            return true;
        } catch (IllegalArgumentException e) {
            Toast.makeText(
                    this,
                    "Désolé, je n'ai pas pu lire ce morceau : problème dans le code",
                    1000).show();
        } catch (IllegalStateException e) {
            if (reTryIfFail) {
                mediaPlayer = new MediaPlayer();
                return tryToLoadSongInPlayer(songUrl, false);
            } else {
                e.printStackTrace();
                mediaPlayer = new MediaPlayer();
                buffer.setText("Shiit happens :(");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radio_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_song_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuRadioHasard:
            page = data.retrieveShuffledNLinks(Segment.MP3,
                    prefs.getInt("radioPreferences.nbSongs", 10));
            refreshList();
            return true;
        case R.id.menuRadioPreferences:
            Intent intent = new Intent(getBaseContext(),
                    RadioPreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuSongDownload:
            Toast.makeText(this, "Le téléchargement est en cours ...", 1000)
                    .show();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    http.downloadFileAndWriteItOnDevice(page.findInList(0)
                            .getUrl(), "test.mp3", null);
                }

            }).start();

            return true;
        }
        return false;
    }

    public void refreshList() {
        ((RadioAdapter) getListAdapter()).notifyDataSetChanged();
    }
}
