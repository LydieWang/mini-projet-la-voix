package com.polytech.di.tianxue.voix_analyse;

import android.media.MediaPlayer;
import android.os.Environment;

/**
 * Created by Administrator on 13/11/2017.
 */

public class AudioPlayer {

    private static final String FILE_NAME = "New Record.wav";
    private String filePath = Environment.getExternalStorageDirectory().getPath();
    private MediaPlayer mediaPlayer  =   new MediaPlayer();
    private boolean isPaused = false;
    private boolean isStopped = false;

    public boolean setData(){
        try {
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                throw (new Exception("Fail to set data of AudioPlayer ! "));
            } else {
                mediaPlayer.setDataSource(filePath + "/" + FILE_NAME);
                mediaPlayer.setLooping(true);
                //duration = mediaPlayer.getDuration();
            }
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void play(){
        try {
            if(!isPaused && !isStopped){ // if it's the first time to play this audio file
                if (!setData()) {
                    throw (new Exception("Fail to set data of AudioPlayer ! "));
                } else {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            }else if(isStopped){ // if the state is STOP
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0); // return to beginning
                mediaPlayer.start();
            }else if (isPaused && !isStopped){ // if the state is PAUSE
                mediaPlayer.start();
            }
            isPaused = false;
            isStopped = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mediaPlayer.isPlaying() && !isPaused && !isStopped){
            mediaPlayer.pause();
        }
        isPaused = true;
    }

    public void stop(){
        mediaPlayer.stop();
        isStopped = true;
    }

    public void destroy(){
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }
}
