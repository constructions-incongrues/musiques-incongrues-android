package com.headbangers.mi.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.headbangers.mi.activity.thread.DownloadFileAsyncTask;
import com.headbangers.mi.model.DownloadObject;

public class DownloadManager {

    public void startDownload(final Activity context, final String storagePath,
            final String title, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(
                "Attention ! Une fois débuté, un téléchargement NE PEUT PAS être annulé. Voulez-vous continuer ?")
                .setCancelable(true)
                .setPositiveButton("Je télécharge !",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new DownloadFileAsyncTask(context,
                                        new DownloadObject(title, url),
                                        storagePath).execute();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Oups! J'annule...",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
