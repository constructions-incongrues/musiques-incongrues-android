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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.RadioPreferencesActivity;
import com.headbangers.mi.activity.thread.DownloadFileAsyncTask;
import com.headbangers.mi.activity.thread.LoadRadioTracksAsyncTask;
import com.headbangers.mi.constant.PreferencesKeys;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.DownloadObject;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.tools.AudioPlayer;
import com.headbangers.mi.tools.ShareByMail;

public class RadioActivity extends GuiceListActivity {
    // private static String TAG = "RadioActivity";

    private AudioPlayer audioPlayer;

    protected SharedPreferences prefs;

    @Inject
    private DataAccessService data;

    private DataPage page = new DataPage();

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

        setListAdapter(new RadioAdapter(RadioActivity.this));
        audioPlayer = AudioPlayer.getInstance(this, progressBar, buffer);
        audioPlayer.setPlaylist(page);
        
        loadAsyncList(LoadRadioTracksAsyncTask.SEARCH_TYPE_FIRST, 0);

        barStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.stopPlaylist();
                tellListToRefresh();
            }
        });

        barRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadAsyncList(LoadRadioTracksAsyncTask.SEARCH_TYPE_FIRST, 0);
            }
        });

        barNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.nextSong();
                tellListToRefresh();
            }
        });

        barPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.previousSong();
                tellListToRefresh();
            }
        });

        barPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.playOrPauseSong();
                tellListToRefresh();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        audioPlayer.playSong(position);
        tellListToRefresh();
    }

    public class RadioAdapter extends ArrayAdapter<MILinkData> {

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
            ImageView songIcon = (ImageView) row.findViewById (R.id.songIcon);

            if (audioPlayer.getCurrentSongNumber() == position){
                songIcon.setImageResource(R.drawable.bonhome);
            } else {
                songIcon.setImageResource(R.drawable.song);
            }

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_song_menu, menu);
        menu.setHeaderTitle("Que faire ?");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuRadioHasard:
            loadAsyncList(LoadRadioTracksAsyncTask.SEARCH_TYPE_RANDOM, 0);
            return true;
        case R.id.menuRadioPreferences:
            Intent intent = new Intent(getBaseContext(),
                    RadioPreferencesActivity.class);
            startActivity(intent);
            return true;
        case R.id.menuRadioNext10:
            int nb = prefs.getInt(PreferencesKeys.radioNumber,
                    PreferencesKeys.radioNumberDefault);
            loadAsyncList(LoadRadioTracksAsyncTask.SEARCH_TYPE_PAGE,
                    AudioPlayer.currentOffset + nb);
            AudioPlayer.currentOffset += nb;
            return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        MILinkData data = page.findInList(menuInfo.position);

        switch (item.getItemId()) {
        case R.id.menuSongDownload:

            new DownloadFileAsyncTask(this, new DownloadObject(data.getTitle(),
                    data.getUrl()), prefs.getString(
                    PreferencesKeys.radioDlPath,
                    PreferencesKeys.radioDlPathDefault)).execute();
            return true;
            
        case R.id.menuSongShare:
            new ShareByMail().shareIt(this, data);
            return true;
        }
        return false;
    }
    
    private void tellListToRefresh (){
        ((RadioAdapter)(getListView().getAdapter())).notifyDataSetChanged();
    }

    public void fillList(DataPage page) {

        if (page != null) {
            RadioAdapter adapter = (RadioAdapter) getListAdapter();
            adapter.clear();
            this.page = page;
            audioPlayer.setPlaylist(page);
            for (MILinkData link : page.getData()) {
                adapter.add(link);
            }
        } else {
            Toast.makeText(this, "Problème lors du chargement, réessayez !",
                    1000).show();
        }
    }

    public void loadAsyncList(int type, int offset) {
        new LoadRadioTracksAsyncTask(this, data).execute(type, prefs
                .getInt(PreferencesKeys.radioNumber,
                        PreferencesKeys.radioNumberDefault), offset);
    }
}
