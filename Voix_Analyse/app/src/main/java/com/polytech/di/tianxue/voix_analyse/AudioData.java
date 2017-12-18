package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 08/12/2017.
 */

public class AudioData {
    public static short maxAmplitude = 0;
    public static short minAmplitude = 0;
    public static short maxAmplitudeAbs = 0;
    public static double maxAmplitudeFre = 0;
    public static long length;
    public static List<Short> data = new ArrayList<>();
    public static double [] amplitudesFre = {0.0};
    public static double shimmer;

    public static double[] getAmplitudesFre(){
        // do FFT transformation to the audio data
        double[] data_doubleFFT = FFT.getFFT(AudioData.data);
        amplitudesFre = FFT.getAmplitudes(data_doubleFFT);
        return amplitudesFre;
    }

    public static int getMaxAmplitudeAbs(){
        if (maxAmplitude > Math.abs(minAmplitude)) {
            maxAmplitudeAbs = maxAmplitude;
        } else {
            maxAmplitudeAbs = (short) Math.abs(minAmplitude);
        }
        return maxAmplitudeAbs;
    }


    public static double getShimmer(){
        List<Short> peaks = new ArrayList<>();
        int A_diff_sum = 0;
        long A_sum = 0;

        // get peaks (relative)
        for(int i = 1; i < length - 1; i ++){
            if(data.get(i) > 0 && data.get(i) > data.get(i-1) && data.get(i) > data.get(i+1)){
                peaks.add(data.get(i));
            }
        }

        // get shimmer
        for(int i = 0; i < peaks.size() - 1; i++){
            A_diff_sum += Math.abs(peaks.get(i+1) - peaks.get(i));
            A_sum += peaks.get(i);
        }
        A_sum += peaks.get(peaks.size() - 1);
        shimmer = (double)(A_diff_sum / (peaks.size() - 1))/(double)(A_sum / peaks.size());
        return shimmer;
    }
}
