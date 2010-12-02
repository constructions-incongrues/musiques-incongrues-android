package com.headbangers.mi.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.headbangers.mi.service.RSSAccessService;

public abstract class BaseFeedParser implements RSSAccessService {

    // names of the XML tags
    public static final String PUB_DATE = "pubDate";
    public static final  String DESCRIPTION = "description";
    public static final  String LINK = "link";
    public static final  String TITLE = "title";
    public static final  String ITEM = "item";
    public static final String CONTENT = "content";
    public static final String CONTENT_ENCODED = "content:encoded";
    
    protected URL feedUrl;
    
    protected void setFeedUrl (String feed){
        try {
            this.feedUrl = new URL(feed);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream getInputStream() {
        try {
            return feedUrl.openConnection().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

