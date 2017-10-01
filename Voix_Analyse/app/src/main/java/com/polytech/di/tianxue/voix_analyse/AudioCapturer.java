package com.polytech.di.tianxue.voix_analyse;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 30/09/2017.
 */

public class AudioCapturer {
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int AUDIO_SAMPLE_RATE = 44100;
    private static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private int minBufferSize = 0;
    private  Thread captureThread;
    private boolean isCaptureStarted = false;
    private static final String TAG = "AudioCapturer";
    private byte[] audioData;

    public boolean startCapture(){
        return startCapture(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {

        if(isCaptureStarted){
            Log.e(TAG, "AudioCapturer is already started !");
        }
        minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid value of minBufferSize !");
            return false;
        }
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig,audioFormat, minBufferSize);
        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord uninitialized !");
            return false;
        }
        audioRecord.startRecording();
        captureThread = new Thread(new AudioCaptureRunnable());
        captureThread.start();
        isCaptureStarted = true;

        return true;
    }

    public void stopCapture() {

        if(isCaptureStarted){
            try {
                captureThread.interrupt();
                captureThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                audioRecord.stop();
            }

            audioRecord.release();
            isCaptureStarted = false;
            audioRecord = null;
            captureThread = null;

            Log.d(TAG, "AudioCapturer successfully stopped !");
        }
    }

    public byte [] getAudioData(){
        return audioData;
    }

    private class AudioCaptureRunnable implements Runnable{
        @Override
        public void run() {

            FileOutputStream os = null;

            try {
                os = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath(),"NEW"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (isCaptureStarted) {
                audioData = new byte[minBufferSize];

                int readState = audioRecord.read(audioData, 0, minBufferSize);
                if (readState == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG , "Error ERROR_INVALID_OPERATION");
                }
                else if (readState == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG , "Error ERROR_BAD_VALUE");
                }else{
                    try {
                        /* write the audio data into a file */
                        os.write(audioData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
