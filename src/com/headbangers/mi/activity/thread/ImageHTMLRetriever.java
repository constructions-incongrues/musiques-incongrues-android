package com.headbangers.mi.activity.thread;

import com.headbangers.mi.tools.DrawableManager;

import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

public class ImageHTMLRetriever implements ImageGetter{

    private static DrawableManager drawableManager = new DrawableManager();
    
    @Override
    public Drawable getDrawable(String source) {
        return drawableManager.fetchDrawable(source);
    }

}
