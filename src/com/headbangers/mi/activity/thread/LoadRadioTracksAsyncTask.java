package com.headbangers.mi.activity.thread;

import com.headbangers.mi.activity.RadioActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;

public class LoadRadioTracksAsyncTask extends
        GenericLoadFromWebserviceAsyncTask {

    public LoadRadioTracksAsyncTask(RadioActivity context,
            DataAccessService data) {
        super(Segment.MP3, data);
        this.context = context;
    }

    @Override
    protected DataPage doInBackground(Integer... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(DataPage result) {
        ((RadioActivity)context).fillList(result);
        super.onPostExecute(result);
    }

}
