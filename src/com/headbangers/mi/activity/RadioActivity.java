package com.headbangers.mi.activity;

import java.io.IOException;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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

    @Inject
    private DataAccessService data;

    private DataPage page;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaPlayer.OnPreparedListener mediaPlayerAsyncLauncher = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            Toast.makeText(RadioActivity.this, "Le morceau démarre !", 1000)
                    .show();
        }               
    };

    @InjectView(R.id.barStop)
    private ImageButton barStop;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);

        // TODO : chercher les morceaux dans une base : seul le bouton refresh
        // va chercher sur le net
        page = data.retrieveLastNLinks(10);

        setListAdapter(new RadioAdapter(this));

        barStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopSong(false); // TODO true pour faire un release des
                                 // ressources
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

            return row;
        }
    }

    protected void playSong(String songUrl) {

        stopSong(false);

        try {
            mediaPlayer.setDataSource(this, Uri.parse(songUrl));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayerAsyncLauncher);
            Toast.makeText(this, "En cours de chargement ...", 1000).show();

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Désolé, je n'ai pas pu lire ce morceau :(",
                    1000).show();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Désolé, je n'ai pas pu lire ce morceau :(",
                    1000).show();
            e.printStackTrace();
        } catch (SecurityException e) {
            Toast.makeText(this, "Désolé, je n'ai pas pu lire ce morceau :(",
                    1000).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Désolé, je n'ai pas pu lire ce morceau :(",
                    1000).show();
            e.printStackTrace();
        }
    }

    protected void stopSong(boolean itsOver) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        if (itsOver) {
            mediaPlayer.release();
        }

    }
}
