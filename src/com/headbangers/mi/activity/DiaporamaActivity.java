package com.headbangers.mi.activity;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.DiaporamaPreferencesActivity;
import com.headbangers.mi.activity.thread.LoadDiaporamaAsyncTask;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.tools.DrawableManager;

public class DiaporamaActivity extends GuiceActivity {

    @InjectView(R.id.diaporamaGallery)
    private Gallery diapoGallery;

    @InjectView(R.id.diaporamaZoom)
    private ImageView zoomImage;

    @InjectView(R.id.diaporamaBarZoomIn)
    private ImageButton zoomIn;

    @InjectView(R.id.diaporamaBarZoomOut)
    private ImageButton zoomOut;

    protected SharedPreferences prefs;

    @Inject
    private DataAccessService data;
    @Inject
    protected HttpService http;
    @Inject
    protected DrawableManager drawableManager;

    private DataPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diaporama);
        prefs = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        // page = data.retrieveLastNLinks(Segment.IMAGES, 10);
        loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_FIRST, 0);
        diapoGallery.setAdapter(new DiaporamaGalleryAdapter(this));
        diapoGallery
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @SuppressWarnings("rawtypes")
                    public void onItemClick(AdapterView parent, View v,
                            int position, long id) {
                        ImageView galleryImage = (ImageView) v;
                        zoomImage.setImageDrawable(galleryImage.getDrawable());
                        zoomImage.setScaleType(ScaleType.FIT_CENTER);
                    }

                });

        zoomIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                zoomImage.getImageMatrix().postScale(1.25f, 1.25f, 0, 0);
                zoomImage.invalidate();
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                zoomImage.getImageMatrix().postScale(1/1.25f, 1/1.25f, 0, 0);
                zoomImage.invalidate();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diaporama_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuDiaporamaPreferences:
            Intent intent = new Intent(getBaseContext(),
                    DiaporamaPreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    class DiaporamaGalleryAdapter extends BaseAdapter {

        private Activity context;

        public DiaporamaGalleryAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return page != null ? page.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return page != null ? page.findInList(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {
            final ImageView i = new ImageView(context);
            if (page != null) {
                MILinkData image = page.findInList(position);

                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new Gallery.LayoutParams(150, 100));
                i.setImageResource(R.drawable.loading);
                drawableManager.fetchDrawableOnThread(image.getUrl(), i);
            }
            return i;

        }

    }

    public void loadAsyncList(int type, int offset) {
        new LoadDiaporamaAsyncTask(this, data, drawableManager).execute(type,
                prefs.getInt("radioPreferences.nbImages", 10), offset);
    }

    public void fillGallery(DataPage page) {
        if (page != null) {
            this.page = page;
            ((BaseAdapter) this.diapoGallery.getAdapter())
                    .notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Problème lors du chargement, réessayez !",
                    1000).show();
        }
    }
}
