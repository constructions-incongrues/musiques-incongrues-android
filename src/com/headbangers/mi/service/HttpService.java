package com.headbangers.mi.service;

import java.io.InputStream;


public interface HttpService {

    InputStream downloadFile(String fileUrl);
    
}
