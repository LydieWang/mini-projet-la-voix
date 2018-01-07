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

    public static double [] amplitudesFre = {0.0};
    public static double shimmer;
    public static double jitter;
    private static List<Integer> pitchPos = new ArrayList<>();
    private static List<Integer> troughPos = new ArrayList<>();
    private static List<Integer> selectedPos;
    private static List<Integer> periods = new ArrayList<>();
    //private static List<Integer> ampPk2Pk = new ArrayList<>();
    private final static double RATIO = 0.3;
    private final static double RATIO_THRESHOLD = 0.9;
    private static int selectedNum;

    public static void processData(){
        // process the original data in cutting the beginning and the last part
        for(int i = (int)(length * RATIO); i < length * (1 - RATIO); i ++){
            data_processed.add(data.get(i));
        }

        try {
            // get pitch positions of processed data
            getPitchPositions();
            getTroughPositions();
            getJitter();
        }catch (Exception e){
            e.printStackTrace();
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

    private static List<Integer> getPitchPositions(){
        short pitchInit = 0;
        for(int i = (int)(data_processed.size() * 0.4); i < data_processed.size() * 0.6; i ++){
            // initializer the pitch at the beginning
            if(pitchInit < data_processed.get(i)){
                pitchInit = data_processed.get(i);
            }
        }
        // set the threshold
        double threshold = pitchInit * RATIO_THRESHOLD;
        short maxAmp = 0;
        for(int i = 0; i < data_processed.size(); i++){
            // pass the useless data (lower than threshold)
            if(data_processed.get(i) > threshold) {
                while (data_processed.get(i) > threshold) {
                    if (maxAmp < data_processed.get(i)) {
                        maxAmp = data_processed.get(i);
                    }
                    i++;
                }
                // add the position into the list
                pitchPos.add(i-1);
                maxAmp = 0;
            }
        }
        return pitchPos;
    }

    private static List<Integer> getTroughPositions(){
        short troughInit = 0;
        for(int i = (int)(data_processed.size() * 0.4); i < data_processed.size() * 0.6; i ++){
            // initializer the pitch at the beginning
            if(troughInit > data_processed.get(i)){
                troughInit = data_processed.get(i);
            }
        }
        // set the threshold
        double threshold = troughInit * RATIO_THRESHOLD;
        short minAmp = 0;
        for(int i = 0; i < data_processed.size(); i++){
            // pass the useless data (lower than threshold)
            if(data_processed.get(i) < threshold) {
                while (data_processed.get(i) < threshold) {
                    if (minAmp > data_processed.get(i)) {
                        minAmp = data_processed.get(i);
                    }
                    i++;
                }
                // add the position into the list
                troughPos.add(i-1);
                minAmp = 0;
            }
        }
        return troughPos;
    }

    private static List<Integer> getPeriods(List<Integer> extremePos){
        List<Integer> T = new ArrayList<Integer>();
        for(int i = 0; i < extremePos.size() - 1; i ++){
            // calculate each period and add it into list
            T.add(extremePos.get(i+1) - extremePos.get(i));
        }
        return T;
    }

    private static double testJitter(List<Integer> extremePos){
        List<Integer> T ;
        double T_diff_sum = 0.0;
        double T_sum = 0.0;

        try {
            T = getPeriods(extremePos);
            // get jitter
            for(int i = 0; i < T.size() - 1; i++){
                Log.v("t(i)-t(i+1)", String.valueOf(Math.abs(T.get(i) - T.get(i+1))));
                T_diff_sum += Math.abs(T.get(i) - T.get(i+1));
                T_sum += T.get(i);
            }
            // add the last period into sum
            T_sum += T.get(T.size()-1);

            // calculate jitter (relative)
            jitter = (T_diff_sum / (double)(T.size() - 1)) / (T_sum / (double)T.size()) * 100;
        }catch (Exception e){
            e.printStackTrace();
        }
        return jitter;
    }

    public static double getJitter(){
        double jitterPitch = testJitter(pitchPos);
        double jitterTrough = testJitter(troughPos);

        Log.v("pitchPos",String.valueOf(pitchPos));
        Log.v("troughPos",String.valueOf(troughPos));

        double mean = (jitterPitch + jitterTrough) / 2;
        double variance = Math.sqrt((jitterTrough - mean)*(jitterTrough - mean) + (jitterPitch - mean)*(jitterPitch - mean));
        Log.v("jitterPitch",String.valueOf(jitterPitch));
        Log.v("jitterTrough",String.valueOf(jitterTrough));
        if(variance < 1.0){
            periods = getPeriods(pitchPos);
            selectedPos = pitchPos;
            selectedNum = 0;
            Log.v("selectedPos",String.valueOf(selectedPos));
            return mean;
        }
        // select the smallest jitter as the correct one
        if(jitterPitch < jitterTrough){
            periods = getPeriods(pitchPos);
            selectedPos = pitchPos;
            selectedNum = 0;
            Log.v("selectedPos",String.valueOf(selectedPos));
            return jitterPitch;
        }
        else {
            periods = getPeriods(troughPos);
            selectedPos = troughPos;
            selectedNum = 1;
            Log.v("selectedPos",String.valueOf(selectedPos));
            return jitterTrough;
        }
    }

    /*
    public static List<Integer> getAmpPk2Pk(){
        // every pitch position indicate where the highest amplitude in this period
        for(int i = 0; i < pitchPos.size() - 1; i++){
            int max = data_processed.get(pitchPos.get(i));
            int min = 0;
            // j indicate the positions in one period
            for(int j = pitchPos.get(i); j < pitchPos.get(i+1); j++){
                // find the lowest amplitude
                if(min > data_processed.get(j)){
                    min = data_processed.get(j);
                }
            }
            // add peak-to-peak amplitude to the list
            ampPk2Pk.add(max - min);
        }

        Log.v("pitchPos",String.valueOf(pitchPos));
        Log.v("pk2pk",String.valueOf(ampPk2Pk));
        return ampPk2Pk;
    }
    */
    public static double getShimmer(){

        long A_diff_sum = 0;
        long A_sum = 0;
        List<Integer> ampPk2Pk = new ArrayList<>();
        Log.v("selectedPos",String.valueOf(selectedPos));
        try {
            if(selectedNum == 0){
                // every pitch position indicate where the highest amplitude in this period
                for(int i = 0; i < selectedPos.size() - 1; i++){
                    int max = data_processed.get(selectedPos.get(i));
                    int min = 0;
                    // j indicate the positions in one period
                    for(int j = selectedPos.get(i); j < selectedPos.get(i+1); j++){
                        // find the lowest amplitude
                        if(min > data_processed.get(j)){
                            min = data_processed.get(j);
                        }
                    }
                    // add peak-to-peak amplitude to the list
                    ampPk2Pk.add(max - min);
                }
            }else if(selectedNum == 1){
                // every pitch position indicate where the lowest amplitude in this period
                for(int i = 0; i < selectedPos.size() - 1; i++){
                    int min = data_processed.get(selectedPos.get(i));
                    int max = 0;
                    // j indicate the positions in one period
                    for(int j = selectedPos.get(i); j < selectedPos.get(i+1); j++){
                        // find the lowest amplitude
                        if(max < data_processed.get(j)){
                            max = data_processed.get(j);
                        }
                    }
                    // add peak-to-peak amplitude to the list
                    ampPk2Pk.add(max - min);
                }
            }

            // get shimmer
            for(int i = 0; i < ampPk2Pk.size() - 1; i++){
                A_diff_sum += Math.abs(ampPk2Pk.get(i) - ampPk2Pk.get(i+1));
                A_sum += ampPk2Pk.get(i);
            }
            // add the last peak-to-peak amplitude into sum
            A_sum += ampPk2Pk.get(ampPk2Pk.size() - 1);

            Log.v("A_sum",String.valueOf(A_sum));
            Log.v("A_diff_sum",String.valueOf(A_diff_sum));
            // calculate shimmer (relative)
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


    /*
    public static double getJitter(){
        List<Integer> T = new ArrayList<Integer>();
        double T_diff_sum = 0.0;
        double T_sum = 0.0;

        try {
            for(int i = 0; i < pitchPos.size() - 1; i ++){
                // calculate each period and add it into list
                T.add(pitchPos.get(i+1) - pitchPos.get(i));
            }
            Log.v("T", String.valueOf(T));
            // get jitter
            for(int i = 0; i < T.size() - 1; i++){
                Log.v("t(i)-t(i+1)", String.valueOf(Math.abs(T.get(i) - T.get(i+1))));
                T_diff_sum += Math.abs(T.get(i) - T.get(i+1));
                T_sum += T.get(i);
            }
            // add the last period into sum
            T_sum += T.get(T.size()-1);

            // calculate jitter (relative)
            jitter = (T_diff_sum / (double)(T.size() - 1)) / (T_sum / (double)T.size()) * 100;
        }catch (Exception e){
            e.printStackTrace();
        }

        return jitter;
    }
    */
}
