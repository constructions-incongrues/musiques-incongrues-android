package com.headbangers.mi.activity.thread;

import android.media.MediaPlayer;
import android.widget.ProgressBar;

public class ProgressBarThread implements Runnable {

    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;

    private int current, total;

    public ProgressBarThread(ProgressBar toUpdate, MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.progressBar = toUpdate;
    }

    @Override
    public void run() {

        while (mediaPlayer.isPlaying()
                && (current = mediaPlayer.getCurrentPosition()) <= (total = mediaPlayer
                        .getDuration())) {

            // try {
            // Thread.sleep(100);
            // pourcentage
            progressBar.setProgress((current * 100) / total);
            // } catch (InterruptedException e) {
            // }
        }
        
        progressBar.setProgress(0);

    }

}
