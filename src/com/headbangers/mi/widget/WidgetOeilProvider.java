package com.headbangers.mi.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RemoteViews;

import com.headbangers.mi.R;
import com.headbangers.mi.activity.DiaporamaActivity;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.Segment;
import com.headbangers.mi.service.impl.MIDataAccessJsonServiceImpl;
import com.headbangers.mi.tools.DrawableManager;

public class WidgetOeilProvider extends AppWidgetProvider {

    private DataAccessService data;
    private DrawableManager drawableManager;

    public WidgetOeilProvider() {
        drawableManager = new DrawableManager();
        data = new MIDataAccessJsonServiceImpl(
                "http://data.musiques-incongrues.net",
                "/collections/links/segments/SEGMENT_VALUE/get?format=json");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int widgetId = appWidgetIds[i];

            // récupère une image au hasard
            DataPage page = data.retrieveShuffledNLinks(Segment.IMAGES, 1);
            MILinkData imageData = page.findInList(0);

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_oeil);

            Intent intent = new Intent(context, DiaporamaActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            views.setOnClickPendingIntent(R.id.widgetOeil, pendingIntent);

            if (imageData != null) {
                // TODO forcer plusieurs chargements
                BitmapDrawable drBitmap = (BitmapDrawable) (drawableManager
                        .fetchDrawable(imageData.getUrl()));
                if (drBitmap != null) {
                    views.setImageViewBitmap(R.id.oeil, drBitmap.getBitmap());
                }
            }
            appWidgetManager.updateAppWidget(widgetId, views);
        }

    }
}
