package com.headbangers.mi.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.headbangers.mi.service.HttpService;

public class HttpServiceImpl implements HttpService{

    private String TAG = "HttpServiceImpl";
    
    @Override
    public InputStream downloadFile(String fileUrl) {

        URL fileEncodedUrl = null;
        try {
            fileEncodedUrl = new URL(fileUrl);
            Log.d(TAG, "Récpuration du fichier "+fileUrl);
            HttpURLConnection conn = (HttpURLConnection) fileEncodedUrl
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
