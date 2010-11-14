package com.headbangers.mi;

import roboguice.config.AbstractAndroidModule;

import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.impl.HttpServiceImpl;
import com.headbangers.mi.service.impl.MIDataAccessJsonServiceImpl;

public class MusiqueIncongruesModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(DataAccessService.class).to(MIDataAccessJsonServiceImpl.class);
        bind(HttpService.class).to(HttpServiceImpl.class);
    }

}
