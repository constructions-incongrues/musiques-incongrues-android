package com.headbangers.mi.activity.thread;

import com.headbangers.mi.activity.TelevisionActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;

public class LoadVideosAsyncTask extends GenericLoadFromWebserviceAsyncTask {
    
    public LoadVideosAsyncTask(TelevisionActivity context,
            DataAccessService data) {
        super (Segment.YOUTUBE, data);
        this.context = context;
    }

    @Override
    protected DataPage doInBackground(Integer... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(DataPage result) {
        ((TelevisionActivity)context).fillList(result);
        super.onPostExecute(result);
    }


}
