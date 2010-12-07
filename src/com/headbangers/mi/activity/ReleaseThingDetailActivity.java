package com.headbangers.mi.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.headbangers.mi.R;
import com.headbangers.mi.activity.thread.DownloadFileAsyncTask;
import com.headbangers.mi.constant.PreferencesKeys;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.DownloadObject;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.tools.AudioPlayer;
import com.headbangers.mi.tools.ShareByMail;

public class ReleaseThingDetailActivity extends ThingDetailActivity {

    private AudioPlayer audioPlayer;

    @InjectView(R.id.releaseStreamBar)
    private ProgressBar progressBar;

    @InjectView(R.id.releaseThingDetailBufferText)
    private TextView buffer;

    @InjectView(R.id.releasePlay)
    private ImageButton playPause;
    @InjectView(R.id.releaseStop)
    private ImageButton stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.simpleOnCreate(savedInstanceState);
        setContentView(R.layout.release_thing_detail);

        fillData();

        audioPlayer = AudioPlayer.getInstance(this, progressBar, buffer);

        // TEMP
        List<MILinkData> fakeData = new ArrayList<MILinkData>();
        MILinkData oneInPlaylist = new MILinkData(itemDisplayed.getTitle(),
                itemDisplayed.getEnclosureLink().toString());
        fakeData.add(oneInPlaylist);
        DataPage fakePlaylist = new DataPage(fakeData, 1);

        audioPlayer.setPlaylist(fakePlaylist);

        playPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.playOrPauseSong();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioPlayer.stopSong();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_release_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        MILinkData fakeData = new MILinkData(this.itemDisplayed.getTitle(),
                this.itemDisplayed.getEnclosureLink().toString());

        switch (item.getItemId()) {
        case R.id.menuReleaseSave:
            new DownloadFileAsyncTask(this, new DownloadObject(
                    fakeData.getTitle(), fakeData.getUrl()), prefs.getString(
                    PreferencesKeys.releasesDlPath,
                    PreferencesKeys.releasesDlPathDefault)).execute();

            return true;

        case R.id.menuReleaseShare:
            new ShareByMail().shareIt(this, fakeData);
            return true;
        }

        return false;
    }
}
