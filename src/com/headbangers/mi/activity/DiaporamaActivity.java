package com.headbangers.mi.activity;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

public class DiaporamaActivity extends GuiceActivity implements OnTouchListener {

    public static int currentOffset = 0;
    
    @SuppressWarnings("unused")
    private static final String TAG = "DiaporamaActivity";

    @InjectView(R.id.diaporamaGallery)
    private Gallery diapoGallery;

    @InjectView(R.id.diaporamaZoom)
    private ImageView zoomImage;

    @InjectView(R.id.diaporamaBarZoomIn)
    private ImageButton zoomIn;
    @InjectView(R.id.diaporamaBarZoomOut)
    private ImageButton zoomOut;
    @InjectView(R.id.diaporamaRefresh)
    private ImageButton refresh;

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
                        zoomImage.setScaleType(ScaleType.MATRIX);
                        matrix.set(zoomImage.getImageMatrix());
                    }

                });

        zoomIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Drawable image = zoomImage.getDrawable();
                zoomImage.getImageMatrix().postScale(1.25f, 1.25f,
                        image.getMinimumWidth() / 2,
                        image.getMinimumHeight() / 2);
                matrix.set(zoomImage.getImageMatrix());
                zoomImage.invalidate();
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Drawable image = zoomImage.getDrawable();
                zoomImage.getImageMatrix().postScale(1 / 1.25f, 1 / 1.25f,
                        image.getMinimumWidth() / 2,
                        image.getMinimumHeight() / 2);
                matrix.set(zoomImage.getImageMatrix());
                zoomImage.invalidate();

            }
        });
        
        refresh.setOnClickListener (new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_FIRST, 0);
            }
        });

        zoomImage.setOnTouchListener(this);
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
            
        case R.id.menuDiaporamaHasard:
            loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_RANDOM, 0);
            return true;
            
        case R.id.menuDiaporamaNext10:
            int nb = prefs.getInt("diaporamaPreferences.nbImages", 10);
            loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_PAGE, currentOffset+nb);
            currentOffset += nb;
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
                prefs.getInt("diaporamaPreferences.nbImages", 10), offset);
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

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView image = (ImageView) v;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            mode = DRAG;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            if (oldDist > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - start.x, event.getY()
                        - start.y);
            } else if (mode == ZOOM) {
                float newDist = spacing(event);
                if (newDist > 10f) {
                   matrix.set(savedMatrix);
                   float scale = newDist / oldDist;
                   matrix.postScale(scale, scale, mid.x, mid.y);
                }
             }
            break;

        }

        image.setImageMatrix(matrix);
        return true;
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}
