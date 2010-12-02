package com.headbangers.mi.service;

import java.util.List;

import com.headbangers.mi.model.RSSMessage;

public interface RSSAccessService {

    /**
     * Permet de parser complètement un flux rss et retourne une liste de POJO
     * RSSMessage.
     * @param url
     * @return
     */
    List<RSSMessage> parse(String url);
    
}
