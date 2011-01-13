package com.headbangers.mi.activity;

import roboguice.activity.GuiceActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.headbangers.mi.R;

public class ShortcutActivity extends GuiceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(this, this.getClass().getName());
        
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "hello");
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(
                ShortcutActivity.this, R.drawable.bonhome);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
        getApplicationContext().sendBroadcast(intent);
        
        finish();
    }

}
