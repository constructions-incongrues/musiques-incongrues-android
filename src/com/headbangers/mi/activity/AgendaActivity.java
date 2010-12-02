package com.headbangers.mi.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.thread.LoadAgendaAsyncTask;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;

public class AgendaActivity extends GuiceListActivity{

    @InjectResource (R.string.rssAgendaUrl)
    private String rssUrl;
    
    @Inject
    private RSSAccessService rss;
    
    private List<RSSMessage> things = new ArrayList<RSSMessage>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);
    
        setListAdapter(new AgendaAdapter(this));
        
        loadAsyncList ();
    }
    
    public class AgendaAdapter extends ArrayAdapter<RSSMessage>{
        
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
        if (things != null) {
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
