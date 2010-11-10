package com.headbangers.mi.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import roboguice.inject.InjectResource;
import android.util.Log;

import com.headbangers.mi.R;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;
import com.headbangers.mi.service.DataAccessService;

@SuppressWarnings("unchecked")
public class MIDataAccessJsonServiceImpl extends WebService implements DataAccessService{

    public static String NUM_FOUND_JSON = "num_found";
    
    @InjectResource(R.string.webservice_host)
    private String serviceHost;
    
    @InjectResource(R.string.webservice_service_links_mp3)
    private String service;
    
    public MIDataAccessJsonServiceImpl() {
    }
    
    public MIDataAccessJsonServiceImpl(String host, String service) {
        this.serviceHost = host;
        this.service = service;
    }
    
    @Override
    public DataPage retrieveLastNLinks(int nb) {
        String webServiceUrl = serviceHost+service+"&sort_direction=desc&limit="+nb;
//        String json = "{\"0\":{\"url\":\"http://puyopuyo.lautre.net/mp3/puyopuyo-tanietzgagarina.mp3\",\"domain_parent\":\"lautre.net\",\"domain_fqdn\":\"puyopuyo.lautre.net\",\"mime_type\":\"audio/mpeg\",\"contributed_at\":\"2006-10-07T01:49:53Z\",\"contributor_id\":1,\"contributor_name\":\"Johan\",\"comment_id\":163,\"discussion_id\":23,\"discussion_name\":\"Puyo Puyo - Orphan Tunes From The Monotimes\",\"availability\":\"available\"},\"1\":{\"url\":\"http://puyopuyo.lautre.net/mp3/puyopuyo-madameh.mp3\",\"domain_parent\":\"lautre.net\",\"domain_fqdn\":\"puyopuyo.lautre.net\",\"mime_type\":\"audio/mpeg\",\"contributed_at\":\"2006-10-07T01:49:53Z\",\"contributor_id\":1,\"contributor_name\":\"Johan\",\"comment_id\":163,\"discussion_id\":23,\"discussion_name\":\"Puyo Puyo - Orphan Tunes From The Monotimes\",\"availability\":\"available\"},\"2\":{\"url\":\"http://thebrain.lautre.net/mp3/thebrain71.mp3\",\"domain_parent\":\"lautre.net\",\"domain_fqdn\":\"thebrain.lautre.net\",\"mime_type\":\"audio/mpeg\",\"contributed_at\":\"2006-10-13T01:15:13Z\",\"contributor_id\":1,\"contributor_name\":\"Johan\",\"comment_id\":195,\"discussion_id\":37,\"discussion_name\":\"THE BRAIN 71\",\"availability\":\"unknown\"},\"3\":{\"url\":\"http://w.a.n.t.free.fr/EMISSIONBERADIO/WANT_3.mp3\",\"domain_parent\":\"free.fr\",\"domain_fqdn\":\"w.a.n.t.free.fr\",\"mime_type\":\"audio/mpeg\",\"contributed_at\":\"2006-10-31T20:58:39Z\",\"contributor_id\":1,\"contributor_name\":\"Johan\",\"comment_id\":276,\"discussion_id\":41,\"discussion_name\":\"We Are Not A Toys - Be Radio\",\"availability\":\"available\"},\"4\":{\"url\":\"http://thebrain.lautre.net/mp3/thebrain72.mp3\",\"domain_parent\":\"lautre.net\",\"domain_fqdn\":\"thebrain.lautre.net\",\"mime_type\":\"audio/mpeg\",\"contributed_at\":\"2006-11-08T14:54:43Z\",\"contributor_id\":21,\"contributor_name\":\"Puyo Puyo\",\"comment_id\":319,\"discussion_id\":56,\"discussion_name\":\"The Brain 72\",\"availability\":\"unknown\"},\"num_found\":1433}";
        String json = callHttp(webServiceUrl);
        
        try {
            
            Map<String, Object> all = jsonMapper.readValue(json, Map.class);
            DataPage page = new DataPage();
            for (Entry<String,Object> entry : all.entrySet()){
                
                if (entry.getKey().equals(NUM_FOUND_JSON)){
                    page.setTotal((Integer)entry.getValue());
                } else {
                    page.add (new MILinkData ((Map<String, Object>) entry.getValue()));
                }
            }
            
            
            return page;
            
        } catch (JsonParseException e) {
            Log.d(TAG, "Erreur de parsing JSON",e);
        } catch (JsonMappingException e) {
            Log.d(TAG, "Erreur de mapping JSON",e);
        } catch (IOException e) {
            Log.d(TAG, "Erreur de lecture JSON",e);
        }
        
        return null;
    }

}
