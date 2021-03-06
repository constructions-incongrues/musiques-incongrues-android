package com.headbangers.mi;

import roboguice.config.AbstractAndroidModule;

import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.HttpService;
import com.headbangers.mi.service.RSSAccessService;
import com.headbangers.mi.service.impl.HttpServiceImpl;
import com.headbangers.mi.service.impl.MIDataAccessJsonServiceImpl;
import com.headbangers.mi.service.impl.RSSAccessServiceImpl;

public class MusiqueIncongruesModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(DataAccessService.class).to(MIDataAccessJsonServiceImpl.class);
        bind(HttpService.class).to(HttpServiceImpl.class);
        bind(RSSAccessService.class).to(RSSAccessServiceImpl.class);
    }

}
