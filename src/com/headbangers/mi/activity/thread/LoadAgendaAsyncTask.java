package com.headbangers.mi.activity.thread;

import java.util.List;

import com.headbangers.mi.activity.AgendaActivity;
import com.headbangers.mi.model.RSSMessage;
import com.headbangers.mi.service.RSSAccessService;

public class LoadAgendaAsyncTask extends GenericLoadFromRSSAsyncTask {
    
    public LoadAgendaAsyncTask(AgendaActivity context, String rssUrl,
            RSSAccessService service) {
        super(rssUrl, service);
        
        this.context = context;
    }
    
    @Override
    protected List<RSSMessage> doInBackground(Integer... params) {
        return super.doInBackground(params);
    }
    
    @Override
    protected void onPostExecute(List<RSSMessage> result) {
        ((AgendaActivity)context).fillPage (result);
        super.onPostExecute(result);
    }

}
