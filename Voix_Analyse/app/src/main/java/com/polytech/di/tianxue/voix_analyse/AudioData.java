package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 08/12/2017.
 */

public class AudioData {
    /**
     * The highest amplitude in this audio data
     */
    public static short maxAmplitude = 0;
    /**
     * The lowest amplitude in this audio data
     */
    public static short minAmplitude = 0;
    /**
     * The highest absolute amplitude in this audio data
     */
    public static short maxAmplitudeAbs = 0;
    public static double maxAmplitudeFre = 0;
    public static long length;
    public static List<Short> data = new ArrayList<>();
    public static List<Short> data_processed = new ArrayList<>();
    public static List<Integer> pitchPos = new ArrayList<>();
    public static List<Integer> ampPk2Pk = new ArrayList<>();
    public static double [] amplitudesFre = {0.0};
    public static double shimmer;
    public static double jitter;
    private final static int N_BASE = 440;

    public static void processData(){
        for(int i = (int)(length * 0.1); i < length * 0.9; i ++){
            data_processed.add(data.get(i));
        }
    }

    public static double[] getAmplitudesFre(){
        // do FFT transformation to the audio data
        double[] data_doubleFFT = FFT.getFFT(data);
        // store the amplitudes of frequencies
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

    public static List<Integer> getPeriod(){
        short pitch = 0;
        for(int i = 0; i < N_BASE; i ++){
            if(pitch < data_processed.get(i)){
                pitch = data_processed.get(i);
            }
        }
        double threshold = pitch * 0.9;
        short max = 0;
        for(int i = 0; i < data_processed.size(); i++){
            while (data_processed.get(i) > threshold){
                if(max < data_processed.get(i)){
                    max = data_processed.get(i);
                }
                i++;
            }
            pitchPos.add(i);
            max = 0;
        }
        return pitchPos;
    }

    public static List<Integer> getAmpPk2Pk(){
        short max_base = 0;
        for(int i = 0; i < N_BASE; i ++){
            if(max_base < data_processed.get(i)){
                max_base = data_processed.get(i);
            }
        }
        short min_base = 0;
        for(int i = 0; i < N_BASE; i ++){
            if(min_base > data_processed.get(i)){
                min_base = data_processed.get(i);
            }
        }

        double threshold_max = max_base * 0.9;
        double threshold_min = min_base * 0.9;

        short max = 0;
        short min = 0;
        for(int i = 0; i < data_processed.size(); i++){
            while (data_processed.get(i) > threshold_max || data_processed.get(i) < threshold_min){
                if(max < data_processed.get(i)){
                    max = data_processed.get(i);
                }
                if(min > data_processed.get(i)){
                    min = data_processed.get(i);
                }
                i++;
            }
            Log.v("max",String.valueOf(max));
            Log.v("min",String.valueOf(min));
            ampPk2Pk.add(max - min);
            min = 0;
            max = 0;
        }
        return ampPk2Pk;
    }

    public static double getShimmer(){

        long A_diff_sum = 0;
        long A_sum = 0;

        try {
            getAmpPk2Pk();

            // get shimmer (relative)
            for(int i = 0; i < ampPk2Pk.size() - 1; i++){
                A_diff_sum += Math.abs(ampPk2Pk.get(i) - ampPk2Pk.get(i+1));
                A_sum += ampPk2Pk.get(i);
            }
            // add the last peak
            A_sum += ampPk2Pk.get(ampPk2Pk.size() - 1);

            Log.v("A_sum",String.valueOf(A_sum));
            Log.v("A_diff_sum",String.valueOf(A_diff_sum));
            // calculate shimmer
            shimmer = ((double) A_diff_sum / (double) (ampPk2Pk.size() - 1)) / ((double) A_sum / (double) ampPk2Pk.size()) * 100;
        }catch (Exception e){
            e.printStackTrace();
        }
        /*
        double sum = 0;
        for(int i = 0; i < ampPk2Pk.size() - 1; i++){
            sum += Math.abs(20 * Math.log10((double)ampPk2Pk.get(i) / (double)ampPk2Pk.get(i+1)));
        }
        shimmer = sum / (double)(ampPk2Pk.size() - 1);
        */

        return shimmer;
    }


    public static double getJitter(){
        List<Integer> T = new ArrayList<Integer>();
        double T_diff_sum = 0.0;
        double T_sum = 0.0;

        try {
            getPeriod();

            for(int i = 0; i < pitchPos.size() - 1; i ++){
                T.add(pitchPos.get(i+1) - pitchPos.get(i));
            }

            // get jitter (relative)
            for(int i = 0; i < T.size() - 1; i++){
                //Log.v("t(i)-t(i+1)", String.valueOf(Math.abs(T.get(i) - T.get(i+1))));
                T_diff_sum += Math.abs(T.get(i) - T.get(i+1));
                T_sum += T.get(i);
            }
            T_sum += T.get(T.size()-1);

            jitter = (T_diff_sum / (double)(T.size() - 1)) / (T_sum / (double)T.size()) * 100;
        }catch (Exception e){
            e.printStackTrace();
        }

        return jitter;
    }
}
