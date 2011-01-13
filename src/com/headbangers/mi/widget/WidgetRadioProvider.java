package com.headbangers.mi.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.headbangers.mi.R;

public class WidgetRadioProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int widgetId = appWidgetIds[i];
        
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_radio);
            
            Intent play = new Intent(context, WidgetRadioController.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    play, 0);
            views.setOnClickPendingIntent(R.id.play, pendingIntent);
            
            appWidgetManager.updateAppWidget(widgetId, views);
        }

    }
}
