package com.polytech.di.tianxue.voix_analyse;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 30/09/2017.
 */

public class AudioCapturer {

    /* the parameters of the audio */
    private static final String FILE_NAME = "New Record";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int AUDIO_SAMPLE_RATE = 44100; //Hz
    private static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private File audioFile;
    private int minBufferSize = 0;
    private AudioRecord audioRecord;
    private Thread captureThread;
    private String fillPath = Environment.getExternalStorageDirectory().getPath();

    public boolean startCapture(){
        return startCapture(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {

        try{
            // create a new file to store the audio data
            audioFile = new File(fillPath +"/"+FILE_NAME);
            if(audioFile.exists()){ // if this file already exists, delete it
                if(!audioFile.delete()){ // if fail to delete this file
                    return false;
                }
            }
            if(!audioFile.createNewFile()){ // if fail to create the new file
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

        // set minBufferSize
        minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig,
                audioFormat);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) { // if there is an error
            return false;
        }

        // instantiate an AudioRecord object
        audioRecord = new AudioRecord(audioSource,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                minBufferSize);
        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) { //if there is an error
            return false;
        }

        // start recording
        audioRecord.startRecording();
        // start a new thread
        captureThread = new Thread(new AudioCaptureThread());
        captureThread.start();

        return true;
    }

    public void stopCapture() {

        if(audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            // stop recording
            audioRecord.stop();

            try {
                captureThread.interrupt();
                captureThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class AudioCaptureThread implements Runnable{
        @Override
        public void run() {

            DataOutputStream dos = null;
            short[] audioData = new short[minBufferSize/2];
            try {
                dos = new DataOutputStream(new FileOutputStream(audioFile));
                int readSize;
                while(audioRecord.getRecordingState() == audioRecord.RECORDSTATE_RECORDING){
                    readSize = audioRecord.read(audioData, 0, audioData.length);
                    if(readSize != audioRecord.ERROR_INVALID_OPERATION  ||
                           readSize != audioRecord.ERROR_BAD_VALUE ){
                        for(int i = 0; i < readSize; i++){
                            dos.writeShort(audioData[i]);
                            dos.flush();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(dos != null){
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                audioRecord.release();
                audioRecord = null;
            }
        }
    }
}
