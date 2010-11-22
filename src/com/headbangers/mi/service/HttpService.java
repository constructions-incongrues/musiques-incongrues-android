package com.headbangers.mi.service;

import java.io.InputStream;


public interface HttpService {

    public static String DEFAULT_PATH = "/data/data/com.headbangers.mi/";
    
    InputStream downloadFile(String fileUrl);
    
    /**
     * Permet de télécharger n'importe quel fichier sur le web
     * et de l'enregistrer sur le téléphone.
     * @param fileUrl l'url du fichier http://chiptunes.fr/toto.mp3
     * @param path le dossier dans lequel le fichier sera enregistré. Si null, 
     * une valeur par défaut sera utilisée.
     * @param fileName le nom du fichier sur le téléphone
     * @return
     */
    boolean downloadFileAndWriteItOnDevice (String fileUrl, String fileName, String path);
}
