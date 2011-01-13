package com.headbangers.mi.tools;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.headbangers.mi.activity.thread.ProgressBarThread;
import com.headbangers.mi.model.DataPage;
import com.headbangers.mi.model.MILinkData;

public class AudioPlayer implements OnCompletionListener, OnPreparedListener,
        OnBufferingUpdateListener, OnSeekCompleteListener, OnErrorListener {

    public static String TAG = "AudioPlayer";

    public static AudioPlayer getInstance(Activity context, ProgressBar bar,
            TextView text) {
        if (instance == null) {
            instance = new AudioPlayer(context, bar, text);
        } else {
            instance.init(context, bar, text);
        }
        return instance;
    }
    
    public static AudioPlayer getInstance(Activity context){
        return getInstance(context, null, null);
    }

    private static AudioPlayer instance = null;

    public static MediaPlayer androidMediaPlayer = new MediaPlayer();
    public static int currentOffset = 0;

    private static Integer currentSongNumber = null;
    private static int forced = 0;

    private boolean autoNext = true;

    private DataPage playlist;
    private ProgressBar associatedProgressBar;
    private Activity context;
    private TextView associatedTextView;

    private ProgressBarThread barThread;

    private AudioPlayer(Activity context, ProgressBar bar, TextView text) {
        androidMediaPlayer.setOnPreparedListener(this);
        androidMediaPlayer.setOnBufferingUpdateListener(this);
        androidMediaPlayer.setOnCompletionListener(this);
        androidMediaPlayer.setOnSeekCompleteListener(this);
        androidMediaPlayer.setOnErrorListener(this);

        init(context, bar, text);
    }

    private void init(Activity context, ProgressBar bar, TextView text) {
        this.associatedProgressBar = bar;
        this.associatedTextView = text;
        this.context = context;

        barThread = new ProgressBarThread(associatedProgressBar,
                androidMediaPlayer);
        // en cas de resume on relance le thread, celui-ci mettra (ou pas) la
        // barre à jour
        new Thread(barThread).start();
    }

    public void setPlaylist(DataPage playlist) {
        this.playlist = playlist;
    }

    public void playSong(int songPositionInPlaylist) {

        MILinkData song = playlist.findInList(songPositionInPlaylist);
        Log.d(TAG, "Tentative de lecture de " + song.getUrl());

        currentSongNumber = songPositionInPlaylist;
        stopSong();

        tryToLoadSongInPlayer(song.getUrl(), true);
    }

    public void stopSong() {
        try {
            androidMediaPlayer.stop();
            androidMediaPlayer.reset();
            associatedProgressBar.setSecondaryProgress(0);
            associatedProgressBar.setProgress(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaylist() {
        autoNext = false;
        stopSong();
        currentSongNumber = null;
        associatedTextView.setText("Sélectionnez un morceau dans la liste");
        autoNext = true;
    }

    protected void forceToReplayTheSelectedSong() {
        if (currentSongNumber != null) {
            if (forced <= 5) {
                playSong(currentSongNumber);
            } else {
                Toast.makeText(
                        context,
                        "Il y a un problème avec le morceau"
                                + playlist.findInList(currentSongNumber)
                                        .getTitle()
                                + ". Impossible de le lire après 5 essais. Sorry ...",
                        3000).show();
                forced = 0;
                // nextSong();
            }
        }
    }

    public void startPlaylist() {
        playSong(0);
    }

    public void playOrPauseSong() {
        if (currentSongNumber == null) {
            startPlaylist();
        } else {

            if (androidMediaPlayer.isPlaying()) {
                androidMediaPlayer.pause();
            } else {
                androidMediaPlayer.start();
                new Thread(barThread).start();
            }
        }
    }

    public void nextSong() {
        if (currentSongNumber != null
                && currentSongNumber < playlist.size() - 1) {
            playSong(currentSongNumber + 1);
        }
    }

    public void previousSong() {
        if (currentSongNumber != null && currentSongNumber > 0) {
            playSong(currentSongNumber - 1);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        associatedProgressBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (autoNext) {
            nextSong();
        }
        associatedProgressBar.setProgress(0);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        associatedTextView.setText(playlist.findInList(currentSongNumber)
                .getTitle());
        Toast.makeText(context, "Le morceau démarre !", 1000).show();

        // lancement du thread d'update de la barre
        new Thread(barThread).start();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        androidMediaPlayer.reset();
        forced += 1;
        forceToReplayTheSelectedSong();
        return true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    private boolean tryToLoadSongInPlayer(String songUrl, boolean reTryIfFail) {
        try {
            androidMediaPlayer.setDataSource(songUrl);
            androidMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            androidMediaPlayer.prepareAsync();
            Toast.makeText(context, "En cours de chargement ...", 1000).show();
            associatedTextView.setText("En cours de chargement, patientez...");

            return true;
        } catch (IllegalArgumentException e) {
            Toast.makeText(
                    context,
                    "Désolé, je n'ai pas pu lire ce morceau : problème dans le code",
                    1000).show();
        } catch (IllegalStateException e) {
            if (reTryIfFail) {
                androidMediaPlayer = new MediaPlayer();
                return tryToLoadSongInPlayer(songUrl, false);
            } else {
                e.printStackTrace();
                androidMediaPlayer = new MediaPlayer();
                associatedTextView.setText("Shiit happens :(");
            }
        } catch (IOException e) {
            Toast.makeText(
                    context,
                    "Désolé, je n'ai pas pu lire ce morceau : la chanson n'existe peut-être plus ...",
                    1000).show();
        }

        return false;
    }

    public Integer getCurrentSongNumber() {
        return new Integer(currentSongNumber != null ? currentSongNumber : -1);
    }

}
