package com.headbangers.mi.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.thread.LoadAgendaAsyncTask;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;
import com.headbangers.mi.tools.ShareByMail;

public class AgendaActivity extends GuiceListActivity {

    @InjectResource(R.string.rssAgendaUrl)
    private String rssUrl;

    @InjectView(R.id.agendaRefresh)
    private ImageButton refresh;

    @Inject
    private RSSAccessService rss;

    private List<RSSMessage> things = new ArrayList<RSSMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);

        registerForContextMenu(getListView());
        setListAdapter(new AgendaAdapter(this));

        loadAsyncList();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAsyncList();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_agenda_menu, menu);
        menu.setHeaderTitle("Que faire ?");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        RSSMessage selected = things.get(menuInfo.position);
        switch (item.getItemId()) {
        case R.id.menuAgendaShare:
            new ShareByMail().shareIt(this, new MILinkData(selected.getTitle(),
                    selected.getLink().toString()));
            return true;
        }
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Intent intent = new Intent(this, ThingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("rssMessage", things.get(position));
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public class AgendaAdapter extends ArrayAdapter<RSSMessage> {

        private AgendaActivity context;

        public AgendaAdapter(AgendaActivity context) {
            super(context, R.layout.one_agenda, things);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.one_agenda, null);
            }

            RSSMessage item = things.get(position);

            TextView songTitle = (TextView) row.findViewById(R.id.thingTitle);
            songTitle.setText(item.getTitle());

            return row;
        }

    }

    private void loadAsyncList() {
        new LoadAgendaAsyncTask(this, rssUrl, rss).execute();
    }

    public void fillPage(List<RSSMessage> result) {
        if (result != null) {
            AgendaAdapter adapter = (AgendaAdapter) getListAdapter();
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
