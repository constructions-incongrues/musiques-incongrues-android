package com.headbangers.mi;

import java.util.List;

import com.google.inject.Module;

import roboguice.application.GuiceApplication;

public class MusiquesIncongruesApplication extends GuiceApplication{

    
    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new MusiqueIncongruesModule());
    }
}
