package com.headbangers.mi.activity;

import roboguice.activity.GuiceActivity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class CancelDownloadActivity extends GuiceActivity {

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        
        Bundle intentBundle = this.getIntent().getExtras();
        final Integer notifId = (Integer) intentBundle.get("notificationId");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous annuler le téléchargement ?")
                .setCancelable(true)
                .setPositiveButton("J'annule !",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                notificationManager.cancel(notifId);
                                dialog.cancel();
                                CancelDownloadActivity.this.finish();
                            }
                        })
                .setNegativeButton("Nope",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                CancelDownloadActivity.this.finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
