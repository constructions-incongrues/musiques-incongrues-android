package com.headbangers.mi.activity;

import java.io.Serializable;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.headbangers.mi.R;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.tools.ShareByMail;

public class ThingDetailActivity extends GuiceActivity {

    @InjectView(R.id.thingDetailTitle)
    private TextView thingTitle;

    @InjectView(R.id.thingDetailDesc)
    private TextView thingDescription;

    @InjectView(R.id.thingDetailLink)
    private TextView thingLink;
    
    protected RSSMessage itemDisplayed;
    
    protected SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thing_detail);
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());       

        fillData();
    }

    protected void fillData() {
        Bundle intentBundle = this.getIntent().getExtras();
        if (intentBundle != null) {
            Serializable object = intentBundle.getSerializable("rssMessage");
            if (object instanceof RSSMessage) {

                // remplissage de l'interface
                RSSMessage item = (RSSMessage) object;
                this.itemDisplayed = item;

                thingTitle.setText(item.getTitle());
                Spanned htmlDecoded = Html.fromHtml(item.getDescription());
                        //,new ImageHTMLRetriever(), null);
                thingDescription.setText(htmlDecoded);
                thingLink.setText(item.getLink().toString());

                Linkify.addLinks(thingDescription, Linkify.ALL);
                Linkify.addLinks(thingLink, Linkify.ALL);
            } else {
                Toast.makeText(this,
                        "Erreur: impossible d'afficher les données.", 2000)
                        .show();
            }
        }
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_agenda_menu, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        MILinkData fakeData = new MILinkData(this.itemDisplayed.getTitle(),
                this.itemDisplayed.getLink().toString());

        switch (item.getItemId()) {
        case R.id.menuAgendaShare:
            new ShareByMail().shareIt(this, fakeData);
            return true;
        }

        return false;
    }

    public void simpleOnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());
    }

}
