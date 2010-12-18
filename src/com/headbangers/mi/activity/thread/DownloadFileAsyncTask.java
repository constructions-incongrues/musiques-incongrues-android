package com.headbangers.mi.activity.thread;

import java.util.Random;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.headbangers.mi.R;
import com.headbangers.mi.model.DownloadObject;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.impl.HttpServiceImpl;

public class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void> {

    private Integer notificationId;

    private Activity context;
    private DownloadObject dlObject;
    private String downloadPath;

    private RemoteViews contentView;
    private NotificationManager notificationManager;
    private Notification notification;

    private HttpService http = new HttpServiceImpl();

    public DownloadFileAsyncTask(Activity context, DownloadObject dlObj,
            String downloadPath) {
        this.context = context;
        this.dlObject = dlObj;
        this.downloadPath = downloadPath;

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationId = new Random().nextInt();
    }

    @Override
    protected void onPreExecute() {
        // création de la notification
        contentView = new RemoteViews(context.getPackageName(),
                R.layout.download_notification);
        contentView.setTextViewText(R.id.objectName, dlObject.getName()
                .length() < 40 ? dlObject.getName() : dlObject.getName()
                .substring(0, 39) + "...");
        contentView.setTextViewText(R.id.objectStatus, "En cours ...");
        contentView.setProgressBar(R.id.objectDlBar, 100, 0, true);

        notification = new Notification(R.drawable.pin01, "Téléchargement",
                System.currentTimeMillis());
        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        Intent notificationIntent = new Intent(this.context,
        // CancelDownloadActivity.class);
                this.context.getClass());
        Bundle bundle = new Bundle();
        bundle.putInt("notificationId", notificationId);
        notificationIntent.putExtras(bundle);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this.context.getBaseContext(), 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notificationManager.notify(notificationId, notification);

    }

    @Override
    protected Void doInBackground(Void... params) {
        // téléchargement
        http.downloadFileAndWriteItOnDevice(dlObject.getUrl(),
                dlObject.getName(), downloadPath);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        notificationManager.cancel(notificationId);
        Toast.makeText(context, "Téléchargement terminé!", 1500).show();
    }

}
