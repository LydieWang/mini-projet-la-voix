package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author      Tianxue WANG and Wenli YAN
 * @version     2018.0115
 * @date        08/12/2017
 */

/**
 * The class stocking the important information of the audio data
 */
public class AudioData {
    public static short maxAmplitude = 0; //The highest amplitude in this audio data
    public static short minAmplitude = 0; //The lowest amplitude in this audio data
    public static short maxAmplitudeAbs = 0;//The highest absolute amplitude in this audio data
    //public static double maxAmplitudeFre = 0;
    public static long length;
    public static long length_processed;
    public static List<Short> data = new ArrayList<>();
    public static List<Short> data_processed = new ArrayList<>();
    //public static double [] amplitudesFre = {0.0};

    public final static double RATIO_BEGINNING = 0.3;
    public final static double RATIO_ENDING = 0.9;

    /**
     * The method processing the audio data
     */
    public static void processData(){
        try {
            length_processed  = 0;
            // cut the head and tail of original audio data, only leave a small part of it
            for(int i = (int)(length * RATIO_BEGINNING); i < length * RATIO_ENDING; i ++){
                data_processed.add(data.get(i));
            }
            length_processed = data_processed.size();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The method setting the absolute highest amplitude
     */
    public static void setMaxAmplitudeAbs(){
        if (maxAmplitude > Math.abs(minAmplitude)) {
            maxAmplitudeAbs = maxAmplitude;
        } else {
            maxAmplitudeAbs = (short) Math.abs(minAmplitude);
        }
    }

    /*
    public static double[] getAmplitudesFre(){
        // do FFT transformation to the audio data
        double[] data_doubleFFT = FFT.getFFT(data_processed);
        // store the amplitudes of frequencies
        amplitudesFre = FFT.getAmplitudes(data_doubleFFT);
        return amplitudesFre;
    }
    */
}
