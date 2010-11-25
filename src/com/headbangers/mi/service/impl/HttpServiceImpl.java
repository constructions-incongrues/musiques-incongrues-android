package com.headbangers.mi.service.impl;

import java.io.BufferedInputStream;
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

    @Override
    public boolean downloadFileAndWriteItOnDevice(String fileUrl,
            String fileName, String path) {

        InputStream stream = downloadFile(fileUrl);
        if (stream != null) {

            try {
                BufferedInputStream bis = new BufferedInputStream(stream, 8192);
                // ByteArrayBuffer baf = new ByteArrayBuffer(50);
                //
                // int current = 0;
                // while ((current = bis.read()) != -1) {
                // baf.append((byte) current);
                // }

                File root = Environment.getExternalStorageDirectory();
                root.mkdir();
                File storage = new File (root, "Music/");
                storage.mkdir();
                File file = new File(storage, fileName);

                FileOutputStream fos = new FileOutputStream(file);
                int current;
//                byte[] buffer = new byte[8192];
//                while (bis.read(buffer) != -1){
//                    fos.write(buffer);
//                }

                while ((current = bis.read()) !=  -1){
                    fos.write (current);
                }
                
                // fos.write(baf.toByteArray());
                fos.close();

                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
