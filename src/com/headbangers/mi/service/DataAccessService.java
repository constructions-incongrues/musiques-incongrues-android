package com.headbangers.mi.service;

import com.headbangers.mi.model.DataPage;

public interface DataAccessService {

    DataPage retrieveLastNLinks (int nb);
    
}
