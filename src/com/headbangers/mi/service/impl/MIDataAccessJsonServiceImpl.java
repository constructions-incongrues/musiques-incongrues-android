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
public class MIDataAccessJsonServiceImpl extends WebService implements
        DataAccessService {

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
        String webServiceUrl = serviceHost + service
                + "&sort_direction=desc&limit=" + nb;
        String json = callHttp(webServiceUrl);

        return parseJson(json);
    }
    
    @Override
    public DataPage retrieveShuffledNLinks(int nb) {
        String webServiceUrl = serviceHost + service + "&sort_field=random&limit="+nb;
        String json = callHttp(webServiceUrl);
        
        return parseJson(json);
    }

    private DataPage parseJson(String json) {
        try {
            Map<String, Object> all = jsonMapper.readValue(json, Map.class);
            DataPage page = new DataPage();
            for (Entry<String, Object> entry : all.entrySet()) {

                if (entry.getKey().equals(NUM_FOUND_JSON)) {
                    page.setTotal((Integer) entry.getValue());
                } else {
                    page.add(new MILinkData((Map<String, Object>) entry
                            .getValue()));
                }
            }

            return page;

        } catch (JsonParseException e) {
            Log.d(TAG, "Erreur de parsing JSON", e);
        } catch (JsonMappingException e) {
            Log.d(TAG, "Erreur de mapping JSON", e);
        } catch (IOException e) {
            Log.d(TAG, "Erreur de lecture JSON", e);
        }

        return null;
    }

}
