package com.headbangers.mi.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.GuiceListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.headbangers.mi.R;
import com.headbangers.mi.model.DownloadObject;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.impl.HttpServiceImpl;

public class DownloadManagerActivity extends GuiceListActivity {

    private static List<DownloadObject> objects = new ArrayList<DownloadObject>();

    private static HttpService http = new HttpServiceImpl();
    
    protected SharedPreferences prefs;
    
    public static void addDownload(final String name, final String url) {
        DownloadObject object = new DownloadObject(name, url);
        objects.add(object);

        // Démarrage du thread de téléchargement
        new Thread(new Runnable() {

            @Override
            public void run() {

                http.downloadFileAndWriteItOnDevice(url, name, "Musiques-Incongrues/Musique");
            }

        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_manager);
    }

}
