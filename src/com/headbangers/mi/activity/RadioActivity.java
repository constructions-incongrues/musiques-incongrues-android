package com.headbangers.mi.activity;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.RadioPreferencesActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;
import com.headbangers.mi.tools.AudioPlayer;

public class RadioActivity extends GuiceListActivity {
    // private static String TAG = "RadioActivity";

    private AudioPlayer audioPlayer;

    protected SharedPreferences prefs;

    @Inject
    private DataAccessService data;

    private DataPage page;

    @InjectView(R.id.barStop)
    private ImageButton barStop;
    @InjectView(R.id.barRefresh)
    private ImageButton barRefresh;
    @InjectView(R.id.barNext)
    private ImageButton barNext;
    @InjectView(R.id.barPlay)
    private ImageButton barPlayPause;
    @InjectView(R.id.barPrevious)
    private ImageButton barPrevious;

    @InjectView(R.id.radioBufferText)
    private TextView buffer; // utiliser ce champ comme indicateur des actions

    @InjectView(R.id.streamBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ANDROID TECHNIQUE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);
        registerForContextMenu(getListView());        
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        page = data.retrieveLastNLinks(Segment.MP3,
                prefs.getInt("radioPreferences.nbSongs", 10));
        setListAdapter(new RadioAdapter(this));

        audioPlayer = new AudioPlayer(this, page, progressBar, buffer);

        barStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.stopPlaylist();
            }
        });

        barRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                page = data.retrieveLastNLinks(Segment.MP3,
                        prefs.getInt("radioPreferences.nbSongs", 10));
                notifyListToCleanup();
            }
        });

        barNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.nextSong();
            }
        });

        barPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.previousSong();
            }
        });

        barPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.playOrPauseSong();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        audioPlayer.playSong(position);
        // ImageView songIcon = (ImageView) v.findViewById(R.id.songIcon);
        // if (songIcon!=null){
        // songIcon.setImageResource(R.drawable.grapp01);
        // }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radio_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        TextView title = (TextView) v.findViewById(R.id.songTitle);
        menu.setHeaderTitle(title.getText());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_song_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuRadioHasard:
            page = data.retrieveShuffledNLinks(
                    Segment.MP3,
                    prefs.getInt("radioPreferences.nbSongs",
                            prefs.getInt("radioPreferences.nbSongs", 10)));
            notifyListToCleanup();
            return true;
        case R.id.menuRadioPreferences:
            Intent intent = new Intent(getBaseContext(),
                    RadioPreferencesActivity.class);
            startActivity(intent);
            return true;
        case R.id.menuRadioNext10:
            int nb = prefs.getInt("radioPreferences.nbSongs", 10);
            page = data.retrieveRangeLinks(Segment.MP3,
                    AudioPlayer.currentOffset + nb, nb);
            AudioPlayer.currentOffset += nb;
            notifyListToCleanup();
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
            final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                    .getMenuInfo();

            final MILinkData data = page.findInList(menuInfo.position);
            DownloadManagerActivity.addDownload(data.getTitle(), data.getUrl());
            startActivity(new Intent(this, DownloadManagerActivity.class));
            return true;
        }
        return false;
    }

    private void notifyListToCleanup() {
        audioPlayer.setPlaylist(page);
        ((RadioAdapter) getListAdapter()).notifyDataSetChanged();
    }
}
