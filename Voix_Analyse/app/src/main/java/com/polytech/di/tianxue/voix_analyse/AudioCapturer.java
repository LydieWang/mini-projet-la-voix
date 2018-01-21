package com.polytech.di.tianxue.voix_analyse;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author      Tianxue WANG and Wenli YAN
 * @version     2018.0115
 * @date        30/09/2017
 */

/**
 * The class getting the audio data form the micro
 */
public class AudioCapturer {

    /* the parameters of the audio */
    private static final String FILE_NAME = "New Record.wav";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int AUDIO_SAMPLE_RATE = 44100; //Hz
    private static final int AUDIO_BITS_PER_SECOND = 16; //bits
    private static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private File audioFile;
    private int minBufferSize = 0;
    private AudioRecord audioRecord;
    private Thread captureThread;
    private String filePath = Environment.getExternalStorageDirectory().getPath();
    private RandomAccessFile randomAccessFile = null;
    private WaveFile waveFile = null;

    /**
     * The method starting capturing
     * @return the result of another method
     */
    public boolean startCapture(){
        return startCapture(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    /**
     * The method starting capturing
     * @return if it's successful
     */
    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {

        // clear all the data before starting
        AudioData.data.clear();
        AudioData.length = 0;

        if(createAudioFile()){ // make sure that the file is created
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
        }else {
            return false;
        }
        return true;
    }


    /**
     * The method stopping capturing
     */
    public void stopCapture() {

        if(audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            // stop recording
            audioRecord.stop();
        }
    }

    /**
     * The method creating a new audio file
     * @return
     */
    private boolean createAudioFile(){
        try{
            if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                throw(new Exception("SDCard not found ! "));
            }else {
                // create a new file to store the audio data
                audioFile = new File(filePath + "/" + FILE_NAME);
                if (audioFile.exists()) { // if this file already exists, delete it
                    if (!audioFile.delete()) { // if fail to delete this file
                        return false;
                    }
                }
                if (!audioFile.createNewFile()) { // if fail to create the new file
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        // create a new file to store the audio data
        try {
            randomAccessFile = new RandomAccessFile(audioFile, "rw");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        // make this file to the type WAVE
        waveFile = new WaveFile(randomAccessFile);
        waveFile.addWaveHeader(AUDIO_SAMPLE_RATE, AUDIO_BITS_PER_SECOND);

        return true;
    }

    /**
     * The class starting a new thread for capturing the data
     */
    private class AudioCaptureThread implements Runnable{
        /**
         * The method overridden starting the thread
         */
        @Override
        public void run() {

            short[] audioData = new short[minBufferSize/2];
            int readSize;

            try {
                while(audioRecord.getRecordingState() == audioRecord.RECORDSTATE_RECORDING){
                    // write the audio data to audioData
                    readSize = audioRecord.read(audioData, 0, audioData.length);
                    if(readSize != audioRecord.ERROR_INVALID_OPERATION  ||
                           readSize != audioRecord.ERROR_BAD_VALUE ){

                        for(int i = 0; i < readSize; i++) {
                            // write the content of audioData into file
                            randomAccessFile.writeShort(Short.reverseBytes(audioData[i]));
                            // get the highest amplitude
                            if (audioData[i] > AudioData.maxAmplitude) {
                               AudioData.maxAmplitude = audioData[i];
                            }
                            // get the lowest amplitude
                            if (audioData[i] < AudioData.minAmplitude) {
                                AudioData.minAmplitude = audioData[i];
                            }
                            // copy all the data into AudioData.data
                            AudioData.data.add(audioData[i]);
                        }
                        // update the number of the data
                        AudioData.length +=  readSize;
                    }else {
                        throw (new Exception("Fail to read audio data."));
                    }
                }
            } catch (Exception e) {
                Log.e("Audio Data", e.getMessage());
            }finally {
                audioRecord.release();
                audioRecord = null;

                waveFile.setWaveHeaderChunkSize();
                AudioData.setMaxAmplitudeAbs();
                if(randomAccessFile != null){
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("Audio Data", "Finish Finally");
            }
        }
    }

}
