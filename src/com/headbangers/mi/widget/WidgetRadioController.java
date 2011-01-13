package com.headbangers.mi.widget;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.Toast;

import com.headbangers.mi.service.DataAccessService;
import com.headbangers.mi.service.impl.MIDataAccessJsonServiceImpl;
import com.headbangers.mi.tools.AudioPlayer;

public class WidgetRadioController extends Activity implements OnCompletionListener {

    private AudioPlayer audioPlayer;
    private DataAccessService data;
    
    public WidgetRadioController() {
        audioPlayer = AudioPlayer.getInstance(this);
        data = new MIDataAccessJsonServiceImpl(
                "http://data.musiques-incongrues.net",
                "/collections/links/segments/SEGMENT_VALUE/get?format=json");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "Coucou", 2000).show();
        
        finish();
    }
    
    private void loadAndPlayASong (){
        // chercher une track au hasard
        
        // tenter de la lire
        
        // si echec, on retente avec une autre
    }
    
    @Override
    public void onCompletion(MediaPlayer mp) {
        loadAndPlayASong();
    }

}
