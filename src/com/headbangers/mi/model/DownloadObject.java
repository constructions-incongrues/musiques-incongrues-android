package com.headbangers.mi.model;

public class DownloadObject {
    private String name;
    private String url;
    
    public DownloadObject(String name, String url) {
        this.url = url;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
}
