package com.headbangers.mi.activity.thread;

import com.headbangers.mi.activity.DiaporamaActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;
import com.headbangers.mi.tools.DrawableManager;

public class LoadDiaporamaAsyncTask extends GenericLoadFromWebserviceAsyncTask {

    private DrawableManager drawableManager;

    public LoadDiaporamaAsyncTask(DiaporamaActivity context,
            DataAccessService data, DrawableManager dManager) {
        super(Segment.IMAGES, data);
        this.context = context;
        this.drawableManager = dManager;
    }

    @Override
    protected DataPage doInBackground(Integer... params) {
        DataPage page = super.doInBackground(params);

        if (page != null) {
            drawableManager.fetchDrawable(page.findInList(0).getUrl());
            if (page.size() >= 2) {
                drawableManager.fetchDrawable(page.findInList(1).getUrl());
            }
        }
        return page;
    }

    @Override
    protected void onPostExecute(DataPage result) {
        ((DiaporamaActivity)context).fillGallery(result);
        super.onPostExecute(result);
    }

}
