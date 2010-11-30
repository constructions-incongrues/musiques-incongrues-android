package com.headbangers.mi.activity.thread;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.headbangers.mi.activity.RadioActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;

public class LoadRadioTracksAsyncTask extends
        AsyncTask<Integer, Void, DataPage> {

    private RadioActivity context;
    private ProgressDialog dialog;
    private DataAccessService data;

    public static final int SEARCH_TYPE_FIRST = 0;
    public static final int SEARCH_TYPE_RANDOM = 1;
    public static final int SEARCH_TYPE_PAGE = 2;

    public LoadRadioTracksAsyncTask(RadioActivity context,
            DataAccessService data) {
        this.context = context;
        this.data = data;
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
            page = data.retrieveLastNLinks(Segment.MP3, params[1]);
            break;
        case SEARCH_TYPE_RANDOM:
            page = data.retrieveShuffledNLinks(Segment.MP3, params[1]);
            break;
        case SEARCH_TYPE_PAGE:
            page = data.retrieveRangeLinks(Segment.MP3, params[2], params[1]);
            break;
        }
        return page;
    }

    @Override
    protected void onPostExecute(DataPage result) {
        context.fillList(result);
        dialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(context, "Chargement annulé", 1000);
    }

}
