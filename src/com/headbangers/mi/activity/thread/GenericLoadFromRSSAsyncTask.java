package com.headbangers.mi.activity.thread;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;

public class GenericLoadFromRSSAsyncTask extends AsyncTask<Integer, Void, List<RSSMessage>>{

    protected Activity context;
    protected ProgressDialog dialog;
    protected RSSAccessService rss;
    protected String url;
    
    public GenericLoadFromRSSAsyncTask(String url, RSSAccessService service) {
        this.rss = service;
        this.url = url;
    }
    
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("En cours de chargement");
        dialog.show();
    }
    
    @Override
    protected List<RSSMessage> doInBackground(Integer... params) {
        return rss.parse(this.url);
    }
    
    @Override
    protected void onPostExecute(List<RSSMessage> result) {
        this.dialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(context, "Chargement annulé", 1000);
    }
    
}
