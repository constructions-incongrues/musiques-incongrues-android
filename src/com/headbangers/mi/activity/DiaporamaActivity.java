package com.headbangers.mi.activity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.headbangers.mi.R;
import com.headbangers.mi.activity.preferences.DiaporamaPreferencesActivity;
import com.headbangers.mi.activity.thread.LoadDiaporamaAsyncTask;
import com.headbangers.mi.constant.PreferencesKeys;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.tools.DownloadManager;
import com.headbangers.mi.tools.DrawableManager;
import com.headbangers.mi.tools.ShareByMail;

public class DiaporamaActivity extends GuiceActivity implements OnTouchListener {

    public static int currentOffset = 0;
    public int currentSelected = 0;

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
    @InjectView(R.id.diaporamaImageInfos)
    private TextView imageInfos;

    protected SharedPreferences prefs;

    @Inject
    private DataAccessService data;
    @Inject
    protected HttpService http;
    @Inject
    protected DrawableManager drawableManager;
    @Inject
    private DownloadManager downloadManager;

    private DataPage page = new DataPage();

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
                        zoomImage.setScaleType(ScaleType.FIT_CENTER);
                        zoomImage.setImageDrawable(galleryImage.getDrawable());
                        zoomImage.setScaleType(ScaleType.MATRIX);
                        matrix.set(zoomImage.getImageMatrix());

                        currentSelected = position;
                        MILinkData data = page.findInList(position);
                        imageInfos.setText(data.getContributorName() + " // "
                                + data.getDiscussionTitle());
                        imageInfos.setVisibility(View.VISIBLE);
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

        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_FIRST, 0);
            }
        });

        zoomImage.setOnTouchListener(this);

        detector = new GestureDetector(this,
                new GestureDetector.OnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                            float velocityX, float velocityY) {
                        return false;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }
                });
        detector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(DiaporamaActivity.this, "Reset!", 1500).show();
                zoomImage.setScaleType(ScaleType.FIT_CENTER);
                matrix.set(zoomImage.getImageMatrix());
                return true;
            }
        });

        registerForContextMenu(diapoGallery);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_image_menu, menu);
        menu.setHeaderTitle("Que faire ?");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        MILinkData data = page.findInList(menuInfo.position);

        switch (item.getItemId()) {
        case R.id.menuImageDownload:
            downloadManager.startDownload(this, prefs.getString(
                    PreferencesKeys.diaporamaDlPath,
                    PreferencesKeys.diaporamaDlPathDefault), data.getTitle(),
                    data.getUrl());

            return true;
        case R.id.menuImageShare:
            new ShareByMail().shareIt(this, data);
            return true;
            
        case R.id.menuImageWallpaper:
            Drawable imageDrawable = drawableManager.fetchDrawable(data.getUrl());
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            try {
                wallpaperManager.setBitmap(((BitmapDrawable)imageDrawable).getBitmap());
            } catch (IOException e) {
                Toast.makeText(this, "Désolé, l'image ne semble pas compatible...", 2000).show();
            }
            return true;
        }
        return false;
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
            int nb = prefs.getInt(PreferencesKeys.diaporamaNumber,
                    PreferencesKeys.diaporamaNumberDefault);
            loadAsyncList(LoadDiaporamaAsyncTask.SEARCH_TYPE_PAGE,
                    currentOffset + nb);
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
                prefs.getInt(PreferencesKeys.diaporamaNumber,
                        PreferencesKeys.diaporamaNumberDefault), offset);
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

    // gérer le touch sans tout casser et conserver la compatibilité Android 1.6
    private static Method _getX = null;
    private static Method _getY = null;
    static {
        int sdk = new Integer(VERSION.SDK).intValue();
        if (sdk > 4) {
            try {
                _getX = MotionEvent.class.getMethod("getX",
                        new Class[] { Integer.TYPE });
                _getY = MotionEvent.class.getMethod("getY",
                        new Class[] { Integer.TYPE });
            } catch (Throwable ex) {
                // on est sur un android trop vieux, pas de zoom multitouch
            }
        }
    }

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    protected GestureDetector detector;

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
        boolean eventHandled = false;
        ImageView image = (ImageView) v;

        detector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            mode = DRAG;
            eventHandled = true;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            if (oldDist > 10f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
            }
            eventHandled = true;
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            eventHandled = true;
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
            eventHandled = true;
            break;

        }

        image.setImageMatrix(matrix);
        return eventHandled;
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        if (_getX != null && _getY != null) {
            // float x = event.getX(0) - event.getX(1);
            // float y = event.getY(0) - event.getY(1);
            try {
                float x = ((Float) _getX.invoke(event, new Integer(0)))
                        - ((Float) _getX.invoke(event, new Integer(1)));
                float y = ((Float) _getY.invoke(event, new Integer(0)))
                        - ((Float) _getY.invoke(event, new Integer(1)));
                return FloatMath.sqrt(x * x + y * y);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        if (_getX != null && _getY != null) {
            // float x = event.getX(0) + event.getX(1);
            // float y = event.getY(0) + event.getY(1);
            try {
                float x = ((Float) _getX.invoke(event, new Integer(0)))
                        + ((Float) _getX.invoke(event, new Integer(1)));
                float y = ((Float) _getY.invoke(event, new Integer(0)))
                        + ((Float) _getY.invoke(event, new Integer(1)));
                point.set(x / 2, y / 2);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

}
