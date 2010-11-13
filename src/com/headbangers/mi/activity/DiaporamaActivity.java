package com.headbangers.mi.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;

public class DiaporamaActivity extends GuiceActivity {

    @InjectView(R.id.diaporamaGallery)
    private Gallery diapoGallery;

    @Inject
    private DataAccessService data;

    private DataPage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diaporama);

        page = data.retrieveLastNLinks(Segment.IMAGES, 10);
        diapoGallery.setAdapter(new DiaporamaGalleryAdapter(this));
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

            Bitmap bm = BitmapFactory.decodeStream(downloadImage(page
                    .findInList(position).getUrl()));
            i.setImageBitmap(bm);
            i.setLayoutParams(new Gallery.LayoutParams(150, 100));
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);

            return i;

        }

        private InputStream downloadImage(String imageUrl) {

            URL imageEncodedUrl = null;
            try {
                imageEncodedUrl = new URL(imageUrl);

                HttpURLConnection conn = (HttpURLConnection) imageEncodedUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                
                return conn.getInputStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
