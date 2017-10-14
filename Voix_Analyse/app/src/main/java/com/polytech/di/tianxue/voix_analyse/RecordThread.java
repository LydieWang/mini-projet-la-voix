package com.polytech.di.tianxue.voix_analyse;

import android.widget.RelativeLayout;

/**
 * Created by Administrator on 14/10/2017.
 */

public class RecordThread implements Runnable{
    private AudioCapturer audioCapturer;

    RecordThread(AudioCapturer audioCapturer){
        this.audioCapturer = audioCapturer;
    }
    @Override
    public void run() {
        // start capturing the audio
        audioCapturer = new AudioCapturer();
        audioCapturer.startCapture();
    }

    public void stop(){
        // stop capturing the audio
        audioCapturer.stopCapture();
    }
}
