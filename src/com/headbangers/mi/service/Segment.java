package com.headbangers.mi.service;

public enum Segment {

    ALL("all"),
    MP3("mp3"),
    IMAGES("images"),
    YOUTUBE("youtube");
    
    private String value;
    
    private Segment(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
