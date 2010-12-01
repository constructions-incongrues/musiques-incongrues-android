package com.headbangers.mi.activity.thread;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.headbangers.mi.activity.DiaporamaActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;
import com.headbangers.mi.tools.DrawableManager;

public class LoadDiaporamaAsyncTask extends AsyncTask<Integer, Void, DataPage>{

    public static final int SEARCH_TYPE_FIRST = 0;
    public static final int SEARCH_TYPE_RANDOM = 1;
    public static final int SEARCH_TYPE_PAGE = 2;
    
    private ProgressDialog dialog;
    
    private DiaporamaActivity context;
    private DataAccessService data;
    private DrawableManager drawableManager;
    
    public LoadDiaporamaAsyncTask(DiaporamaActivity context, DataAccessService data, DrawableManager dManager) {
        this.data = data;
        this.context = context;
        this.drawableManager = dManager;
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
            page = data.retrieveLastNLinks(Segment.IMAGES, params[1]);
            break;
        case SEARCH_TYPE_RANDOM:
            page = data.retrieveShuffledNLinks(Segment.IMAGES, params[1]);
            break;
        case SEARCH_TYPE_PAGE:
            page = data.retrieveRangeLinks(Segment.IMAGES, params[2], params[1]);
            break;
        }
        
        if (page !=null){
            drawableManager.fetchDrawable(page.findInList(0).getUrl());
            if (page.size()>=2){
                drawableManager.fetchDrawable(page.findInList(1).getUrl());
            }
        }
        return page;
    }
    
    @Override
    protected void onPostExecute(DataPage result) {
        context.fillGallery (result);
        dialog.dismiss();
    }

}
