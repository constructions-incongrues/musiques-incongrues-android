package com.headbangers.mi.activity;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.headbangers.mi.activity.preferences.TelevisionPreferencesActivity;
import com.headbangers.mi.activity.thread.GenericLoadFromWebserviceAsyncTask;
import com.headbangers.mi.activity.thread.LoadDiaporamaAsyncTask;
import com.headbangers.mi.activity.thread.LoadVideosAsyncTask;
import com.headbangers.mi.constant.PreferencesKeys;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.tools.ShareByMail;

public class TelevisionActivity extends GuiceListActivity {

    @Inject
    private DataAccessService data;

    protected SharedPreferences prefs;

    private DataPage page = new DataPage();
    private int currentOffset = 0;

    @InjectView(R.id.televisionRefresh)
    private ImageButton refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.television);
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        registerForContextMenu(getListView());

        setListAdapter(new TelevisionAdapter(this));
        loadAsyncList(GenericLoadFromWebserviceAsyncTask.SEARCH_TYPE_FIRST, 0);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAsyncList(LoadVideosAsyncTask.SEARCH_TYPE_FIRST, 0);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MILinkData video = page.findInList(position);
        if (video != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video
                    .getUrl())));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.television_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_video_menu, menu);
        menu.setHeaderTitle("Que faire ?");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        MILinkData data = page.findInList(menuInfo.position);

        switch (item.getItemId()) {
        case R.id.menuVideoShare:
            new ShareByMail().shareIt(this, data);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuTelevisionPreferences:
            Intent intent = new Intent(getBaseContext(),
                    TelevisionPreferencesActivity.class);
            startActivity(intent);
            return true;

        case R.id.menuTelevisionHasard:
            loadAsyncList(LoadVideosAsyncTask.SEARCH_TYPE_RANDOM, 0);
            return true;

        case R.id.menuTelevisionNext10:
            int nb = prefs.getInt(PreferencesKeys.televisionNumber,
                    PreferencesKeys.televisionNumberDefault);
            loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_PAGE,
                    currentOffset + nb);
            currentOffset += nb;
            return true;
        }
        return false;
    }

    public class TelevisionAdapter extends ArrayAdapter<MILinkData> {

        private TelevisionActivity context;

        public TelevisionAdapter(TelevisionActivity context) {
            super(context, R.layout.one_video, page.getData());
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.one_video, null);
            }

            MILinkData data = page.findInList(position);

            TextView videoTitle = (TextView) row.findViewById(R.id.videoTitle);
            videoTitle.setText(data.getTitle());
            TextView videoContributor = (TextView) row
                    .findViewById(R.id.videoContributor);
            videoContributor.setText(data.getContributorName() + " // "
                    + data.getContributionDate());
            TextView topicTitle = (TextView) row.findViewById(R.id.topicTitle);
            topicTitle.setText(data.getDiscussionTitle());

            return row;
        }
    }

    public void loadAsyncList(int type, int offset) {
        new LoadVideosAsyncTask(this, data).execute(type, prefs.getInt(
                PreferencesKeys.televisionNumber,
                PreferencesKeys.televisionNumberDefault), offset);
    }

    public void fillList(DataPage page) {

        if (page != null) {
            TelevisionAdapter adapter = (TelevisionAdapter) getListAdapter();
            adapter.clear();
            this.page = page;
            for (MILinkData link : page.getData()) {
                adapter.add(link);
            }
        } else {
            Toast.makeText(this, "Problème lors du chargement, réessayez !",
                    1000).show();
        }
    }
}
