package com.headbangers.mi.activity.thread;

import java.util.List;

import com.headbangers.mi.activity.ReleasesActivity;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;

public class LoadReleasesAsyncTask extends GenericLoadFromRSSAsyncTask {

    public LoadReleasesAsyncTask(ReleasesActivity context, String podcastUrl,
            RSSAccessService service) {
        super(podcastUrl, service);
        this.context = context;
    }
    
    @Override
    protected List<RSSMessage> doInBackground(Integer... params) {
        return super.doInBackground(params);
    }
    
    @Override
    protected void onPostExecute(List<RSSMessage> result) {
        ((ReleasesActivity) context).fillPage (result);
        super.onPostExecute(result);
    }

}
