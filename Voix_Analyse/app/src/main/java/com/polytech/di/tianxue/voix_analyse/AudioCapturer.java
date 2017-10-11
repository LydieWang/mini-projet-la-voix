package com.polytech.di.tianxue.voix_analyse;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile
;
/**
 * Created by Administrator on 30/09/2017.
 */

public class AudioCapturer {

    /* the parameters of the audio */
    private static final String FILE_NAME = "New Record.wav";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int AUDIO_SAMPLE_RATE = 44100; //Hz
    private static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private File audioFile;
    private int minBufferSize = 0;
    private AudioRecord audioRecord;
    private Thread captureThread;
    private String filePath = Environment.getExternalStorageDirectory().getPath();
    private RandomAccessFile randomAccessFile = null;

    public boolean startCapture(){
        return startCapture(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    public boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {

        try{
            // create a new file to store the audio data
            audioFile = new File(filePath +"/"+FILE_NAME);
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

        try {
            randomAccessFile = new RandomAccessFile(audioFile, "rw");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        addWaveHeader();

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
        }
    }

    private class AudioCaptureThread implements Runnable{
        @Override
        public void run() {

            short[] audioData = new short[minBufferSize/2];
            int readSize;

            try {

                while(audioRecord.getRecordingState() == audioRecord.RECORDSTATE_RECORDING){
                    // write the audio data to audioData
                    readSize = audioRecord.read(audioData, 0, audioData.length);
                    //Log.v("Audio Data", String.valueOf(readSize));
                    if(readSize != audioRecord.ERROR_INVALID_OPERATION  ||
                           readSize != audioRecord.ERROR_BAD_VALUE ){

                        for(int i = 0; i < readSize; i++){
                            // print the value of audioData
                            //Log.v("Audio Data", String.valueOf(audioData[i]));

                            // write the content of audioData into file
                            randomAccessFile.writeShort(Short.reverseBytes(audioData[i]));
                        }
                    }else {
                        throw (new Exception("Fail to read audio data."));
                    }
                }

            } catch (Exception e) {
                Log.e("Audio Data", e.getMessage());
            }finally {
                audioRecord.release();
                audioRecord = null;

                setWaveHeaderChunkSize();

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

    private void addWaveHeader(){

        try {
            /* RIFF header */
            randomAccessFile.writeBytes("RIFF"); // riff id
            randomAccessFile.writeInt(0); // riff chunk size *PLACEHOLDER*
            randomAccessFile.writeBytes("WAVE"); // wave type

            /* fmt chunk */
            randomAccessFile.writeBytes("fmt "); // fmt id
            randomAccessFile.writeInt(Integer.reverseBytes(16)); // fmt chunk size
            randomAccessFile.writeShort(Short.reverseBytes((short) 1)); // format: 1(PCM)
            randomAccessFile.writeShort(Short.reverseBytes((short) 1)); // channels: 1
            randomAccessFile.writeInt(Integer.reverseBytes(AUDIO_SAMPLE_RATE)); // samples per second
            randomAccessFile.writeInt(Integer.reverseBytes((int) (AUDIO_SAMPLE_RATE * 16 / 8))); // BPSecond
            randomAccessFile.writeShort(Short.reverseBytes((short) (16 / 8))); // BPSample
            randomAccessFile.writeShort(Short.reverseBytes((short) (16))); // bPSample

            /* data chunk */
            randomAccessFile.writeBytes("data"); // data id
            randomAccessFile.writeInt(0); // data chunk size
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setWaveHeaderChunkSize(){

        try {
            // set RIFF chunk size
            randomAccessFile.seek(4);
            randomAccessFile.writeInt(Integer.reverseBytes((int) (randomAccessFile.length() - 8)));

            // set data chunk size
            randomAccessFile.seek(40);
            randomAccessFile.writeInt(Integer.reverseBytes((int) (randomAccessFile.length() - 44)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //对录音文件进行分析
    /*
    public void frequencyAnalyse(){
        if(audioFile == null){
            return;
        }
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(audioFile));
            //从文件中读出一段数据，这里长度是SAMPLE_RATE，也就是1s采样的数据
            short[] buffer = new short[AUDIO_SAMPLE_RATE];
            for(int i = 0;i < buffer.length; i++){
                buffer[i] = inputStream.readShort();
            }
            short[] data = new short[FFT.FFT_N];

            //为了数据稳定，在这里FFT分析只取最后的FFT_N个数据
            System.arraycopy(buffer, buffer.length - FFT.FFT_N,
                    data, 0, FFT.FFT_N);

            //FFT分析得到频率
            double frequency = FFT.GetFrequency(data);

            Log.v("FFT", "Frequency :" + frequency);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
