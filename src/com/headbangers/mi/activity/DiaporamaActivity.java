package com.headbangers.mi.activity;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.Segment;

public class DiaporamaActivity extends GuiceActivity {

    @InjectView(R.id.diaporamaGallery)
    private Gallery diapoGallery;

    @Inject
    private DataAccessService data;
    @Inject
    protected HttpService http;

    private DataPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diaporama);

        page = data.retrieveLastNLinks(Segment.IMAGES, 10);
        diapoGallery.setAdapter(new DiaporamaGalleryAdapter(this));

        diapoGallery
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    @SuppressWarnings("rawtypes")
                    public void onItemClick(AdapterView parent, View v,
                            int position, long id) {

                    }

                });
    }

    class DiaporamaGalleryAdapter extends BaseAdapter {

        private Activity context;

        public DiaporamaGalleryAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return page.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return page.findInList(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(context);

            if (page.findInList(position).getCachedBitmap() == null) {
                Bitmap bm = BitmapFactory.decodeStream(http.downloadFile(page
                        .findInList(position).getUrl()));
                Bitmap scaledAndCacheable = Bitmap.createScaledBitmap(bm, 500, 500, true);
                page.findInList(position).setCachedBitmap(scaledAndCacheable);
            }

            i.setImageBitmap(page.findInList(position).getCachedBitmap());
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);

            return i;

        }

    }
}
