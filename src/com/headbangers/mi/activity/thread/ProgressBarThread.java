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
        progressBar.setProgress(0);
        try {
            while (mediaPlayer.isPlaying()
                    && (current = mediaPlayer.getCurrentPosition()) <= (total = mediaPlayer
                            .getDuration()) && total > 0) {

                progressBar.setProgress((current * 100)
                        / (total > 0 ? total : 1));
            }
        } catch (IllegalStateException e) {
            // allez ! salut !
        }

    }

}
