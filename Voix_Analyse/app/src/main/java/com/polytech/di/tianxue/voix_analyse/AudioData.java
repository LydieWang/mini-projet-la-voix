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
    public static double jitter;

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

    public static double getShimmer(){
        List<Integer> ampPk2Pk = new ArrayList<>();
        List<Short> ampPositive = new ArrayList<>();
        List<Short> ampNegative = new ArrayList<>();
        long A_diff_sum = 0;
        long A_sum = 0;

        boolean isChangedPos = false;
        boolean isChangedNeg = false;

        // get amplitude peak-to-peak
        for(int i = 0; i < length - 1; i ++){
            //Log.v("Data[i]", String.valueOf(data.get(i)));
            double ratio = 0.0;
            // get ratio, if ratio is too small, we consider that this datum is useless
            if(data.get(i) > 0){
                ratio = (double)data.get(i) / (double) maxAmplitude;
            }else {
                ratio = (double)data.get(i) / (double) minAmplitude;
            }

            if(ratio > 0.05) {
                // get all the positive amplitudes in one period
                if (data.get(i) > 0 && !isChangedPos) {
                    ampPositive.add(data.get(i));
                    // if the next element is no longer positive, change the status
                    if (data.get(i + 1) <= 0) {
                        isChangedPos = true;
                    }
                }
                // get all the negative amplitudes in one period
                if (data.get(i) <= 0 && !isChangedNeg) {
                    ampNegative.add(data.get(i));
                    // if the next element is no longer negative, change the status
                    if (data.get(i + 1) > 0) {
                        isChangedNeg = true;
                    }
                }
            }
            short max = 0;
            short min = 0;
            if(isChangedPos && isChangedNeg){
                // get the max amplitude in this period
                if(ampPositive.size() != 0){
                    max = ampPositive.get(0);
                    for(int j = 0; j < ampPositive.size(); j++){
                        if(max < ampPositive.get(j)){
                            max = ampPositive.get(j);
                        }
                    }
                }
                // get the min amplitude int this period
                if(ampNegative.size() != 0){
                    min = ampNegative.get(0);
                    for(int j = 0; j < ampNegative.size(); j++){
                        if(min > ampNegative.get(j)){
                            min = ampNegative.get(j);
                        }
                    }
                }
                // add the change between max and min into the list ampPk2Pk
                ampPk2Pk.add(max - min);

                // clear this two arrays for the next loop
                ampPositive.clear();
                ampNegative.clear();

                // reset the two boolean values
                isChangedPos = false;
                isChangedNeg = false;
            }
        }

        // get shimmer (relative)
        for(int i = 0; i < ampPk2Pk.size() - 1; i++){
            A_diff_sum += Math.abs(ampPk2Pk.get(i) - ampPk2Pk.get(i+1));
            A_sum += ampPk2Pk.get(i);
        }

        try {
            // add the last peak
            A_sum += ampPk2Pk.get(ampPk2Pk.size() - 1);

            /*
            Log.v("A_diff_sum", String.valueOf(A_diff_sum));
            Log.v("A_sum", String.valueOf(A_sum));
            Log.v("ampPk2Pk", String.valueOf(ampPk2Pk));
            Log.v("A_diff_sum_double", String.valueOf((double)A_diff_sum));
            Log.v("A_sum_double", String.valueOf((double)A_sum));
            */

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
        int size = FFT.FFT_N/2;
        double [] T = new double[size];
        double T_diff_sum = 0.0;
        double T_sum = 0.0;

        getAmplitudesFre();

        try {
            for(int i = 0; i < size; i ++){
                T[i] = 1 / amplitudesFre[i];
            }

            // get jitter (relative)
            for(int i = 0; i < size - 1; i++){
                T_diff_sum += Math.abs(T[i] - T[i+1]);
                T_sum += T[i];
            }

            T_sum += T[size - 1];

            Log.v("T_diff_sum", String.valueOf(T_diff_sum));
            Log.v("T_sum", String.valueOf(T_sum));
            jitter = (T_diff_sum / (double)(size - 1)) / (T_sum / (double)size) * 100;
        }catch (Exception e){
            e.printStackTrace();
        }

        return jitter;
    }
}
