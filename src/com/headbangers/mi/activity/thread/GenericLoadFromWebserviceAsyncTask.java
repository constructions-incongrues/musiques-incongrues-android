package com.headbangers.mi.activity.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;

public abstract class GenericLoadFromWebserviceAsyncTask extends
        AsyncTask<Integer, Void, DataPage> {

    public static final int SEARCH_TYPE_FIRST = 0;
    public static final int SEARCH_TYPE_RANDOM = 1;
    public static final int SEARCH_TYPE_PAGE = 2;

    protected Activity context;
    protected ProgressDialog dialog;
    protected DataAccessService data;
    protected Segment segment;

    protected GenericLoadFromWebserviceAsyncTask(Segment segment,
            DataAccessService data) {
        this.data = data;
        this.segment = segment;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("En cours de chargement");
        dialog.show();
    }

    @Override
    protected DataPage doInBackground(Integer... params) {
        DataPage page = null;

        switch (params[0]) {
        case SEARCH_TYPE_FIRST:
            page = data.retrieveLastNLinks(segment, params[1]);
            break;
        case SEARCH_TYPE_RANDOM:
            page = data.retrieveShuffledNLinks(segment, params[1]);
            break;
        case SEARCH_TYPE_PAGE:
            page = data.retrieveRangeLinks(segment, params[2], params[1]);
            break;
        }

        return page;
    }

    @Override
    protected void onPostExecute(DataPage result){
        dialog.dismiss();
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(context, "Chargement annulé", 1000);
    }
}
