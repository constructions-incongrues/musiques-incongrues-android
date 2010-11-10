package com.headbangers.mi;

import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.impl.MIDataAccessJsonServiceImpl;

import roboguice.config.AbstractAndroidModule;

public class MusiqueIncongruesModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(DataAccessService.class).to(MIDataAccessJsonServiceImpl.class);
    }

}
