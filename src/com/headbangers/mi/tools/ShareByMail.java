package com.headbangers.mi.tools;

import android.app.Activity;
import android.content.Intent;

import com.headbangers.mi.model.MILinkData;

public class ShareByMail {

    public void shareIt(final Activity context, final MILinkData data) {

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "[MUSIQUES-INCONGRUES] T'as vu ça ?");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Partage automatique depuis l'application Musiques-Incongrues Android : "
                        + data.getUrl());
        context.startActivity(Intent.createChooser(emailIntent, "Partager via"));

    }

}
