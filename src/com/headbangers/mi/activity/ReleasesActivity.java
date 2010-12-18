package com.headbangers.mi.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.ReleasesPreferencesActivity;
import com.headbangers.mi.activity.thread.LoadReleasesAsyncTask;
import com.headbangers.mi.constant.PreferencesKeys;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;
import com.headbangers.mi.tools.DownloadManager;
import com.headbangers.mi.tools.ShareByMail;

public class ReleasesActivity extends GuiceListActivity {

    @InjectResource(R.string.podcastReleasesUrl)
    private String podcastUrl;

    @Inject
    private RSSAccessService rss;

    protected List<RSSMessage> things = new ArrayList<RSSMessage>();

    @InjectView(R.id.releasesRefresh)
    private ImageButton refresh;

    protected SharedPreferences prefs;

    @Inject
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.releases);
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        registerForContextMenu(getListView());
        setListAdapter(new ReleasesAdapter(this));
        loadAsyncList();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAsyncList();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, ReleaseThingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("rssMessage", things.get(position));
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_release_menu, menu);
        menu.setHeaderTitle("Que faire ?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.releases_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // WTF ??? jamais appelée ... alors que pour les autres, c'est ok ...
        // bizarre :(
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        RSSMessage selected = things.get(menuInfo.position);
        MILinkData fakeData = new MILinkData(selected.getTitle(), selected
                .getEnclosureLink().toString());
        switch (item.getItemId()) {
        case R.id.menuReleasesPreferences:
            Intent intent = new Intent(getBaseContext(),
                    ReleasesPreferencesActivity.class);
            startActivity(intent);
            return true;
        case R.id.menuReleaseSave:
            downloadManager.startDownload(this, prefs.getString(
                    PreferencesKeys.releasesDlPath,
                    PreferencesKeys.releasesDlPathDefault),
                    fakeData.getTitle(), fakeData.getUrl());

            return true;

        case R.id.menuReleaseShare:
            new ShareByMail().shareIt(this, fakeData);
            return true;
        }
        return false;
    }

    public class ReleasesAdapter extends ArrayAdapter<RSSMessage> {

        private ReleasesActivity context;

        public ReleasesAdapter(ReleasesActivity context) {
            super(context, R.layout.one_release, things);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.one_release, null);
            }

            RSSMessage item = things.get(position);

            TextView releaseTitle = (TextView) row
                    .findViewById(R.id.releaseTitle);
            TextView releaseAuthor = (TextView) row
                    .findViewById(R.id.releaseAuthor);

            releaseTitle.setText(item.getTitle());
            releaseAuthor.setText(item.getAuthor() + " // "
                    + item.getSimpleFormatedDate());

            return row;
        }
    }

    protected void loadAsyncList() {
        new LoadReleasesAsyncTask(this, podcastUrl, rss).execute();
    }

    public void fillPage(List<RSSMessage> result) {
        if (result != null) {
            ReleasesAdapter adapter = (ReleasesAdapter) getListAdapter();
            adapter.clear();
            this.things = result;
            for (RSSMessage item : things) {
                adapter.add(item);
            }
        } else {
            Toast.makeText(this, "Problème lors du chargement, réessayez !",
                    1000).show();
        }

    }
}
