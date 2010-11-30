package com.headbangers.mi.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

import com.headbangers.mi.service.HttpService;

public class HttpServiceImpl implements HttpService {

    private String TAG = "HttpServiceImpl";

    @Override
    public InputStream downloadFile(String fileUrl) {

        URL fileEncodedUrl = null;
        try {
            fileEncodedUrl = new URL(fileUrl);
            Log.d(TAG, "Récpuration du fichier " + fileUrl);
            HttpURLConnection conn = (HttpURLConnection) fileEncodedUrl
                    .openConnection();
            conn.connect();

            return conn.getInputStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean downloadFileAndWriteItOnDevice(InputStream stream,
            String filename, String path) {
        if (stream != null) {

            try {
                BufferedInputStream bis = new BufferedInputStream(stream, 8192);

                File root = Environment.getExternalStorageDirectory();
                root.mkdir();
                File storage = new File(root, path);
                storage.mkdirs();
                File file = new File(storage, filename);

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
                int current;

                while ((current = bis.read()) != -1) {
                    bos.write(current);
                }

                bis.close();
                fos.close();
                bos.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean downloadFileAndWriteItOnDevice(String fileUrl,
            String filename, String path) {

        InputStream stream = downloadFile(fileUrl);
        return downloadFileAndWriteItOnDevice(stream, filename, path);
    }

}
