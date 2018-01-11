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
    public static long length_processed;
    public static List<Short> data = new ArrayList<>();
    public static List<Short> data_processed = new ArrayList<>();
    public static double [] amplitudesFre = {0.0};
    public static double shimmer = 0.0;
    public static double jitter = 0.0;
    public static double f0 = 0.0;


    private static List<Integer> periods;
    private static List<Double> jitters = new ArrayList<>();
    private static List<Double> shimmers = new ArrayList<>();
    private static int selectedNum;
    private final static int MODE_ERROR = 0;
    private final static int MODE_PITCH = 1;
    private final static int MODE_TROUGH = 2;
    private final static double RATIO_AMP_THRESHOLD = 0.8;
    private final static double RATIO_BEGINNING = 0.4;
    private final static double RATIO_ENDING = 0.8;
    private final static int N = 220;

    public static void processData(){
        try {
            length_processed  = 0;
            // cut the head and tail of original audio data, only leave a small part of it
            for(int i = (int)(length * RATIO_BEGINNING); i < length * RATIO_ENDING; i ++){
                data_processed.add(data.get(i));
            }
            length_processed = data_processed.size();

            //calculateShimmersJitters();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static double[] getAmplitudesFre(){
        // do FFT transformation to the audio data
        double[] data_doubleFFT = FFT.getFFT(data_processed);
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

    private static void calculateShimmersJitters(){
        int posStart = 0;
        int posEnd = N;

        // operation in each small part of data
        while(posEnd < length_processed){
            List<Integer> pitchPos = getPitchPositions(posStart, posEnd);
            List<Integer> troughPos = getTroughPositions(posStart, posEnd);
            double jitterPitch = getJitter(pitchPos);
            double jitterTrough = getJitter(troughPos);
            double jitter = selectJitter(jitterPitch, jitterTrough);

            Log.v("jitterPitch", String.valueOf(jitterPitch));
            Log.v("jitterTrough", String.valueOf(jitterTrough));
            Log.v("pitchPos", String.valueOf(pitchPos));
            Log.v("troughPos", String.valueOf(troughPos));
            if(jitter > 0.0) {
                jitters.add(jitter);
            }

            // shimmer
            double shimmer;
            ArrayList<Integer> ampPk2Pk = new ArrayList<>();
            if(selectedNum == MODE_PITCH){
                //peroid = getPeriods(pitchPos);
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
            }else if(selectedNum == MODE_TROUGH){
                //peroid = getPeriods(troughPos);
                // every pitch position indicate where the lowest amplitude in this period
                for(int i = 0; i < troughPos.size() - 1; i++){
                    int min = data_processed.get(troughPos.get(i));
                    int max = 0;
                    // j indicate the positions in one period
                    for(int j = troughPos.get(i); j < troughPos.get(i+1); j++){
                        // find the lowest amplitude
                        if(max < data_processed.get(j)){
                            max = data_processed.get(j);
                        }
                    }
                    // add peak-to-peak amplitude to the list
                    ampPk2Pk.add(max - min);
                }
            }
            if(selectedNum != MODE_ERROR) {
                shimmer = getShimmer(ampPk2Pk);
                if(shimmer > 0.0){
                    shimmers.add(shimmer);
                }
            }

            posStart += N;
            posEnd += N;
        }
        Log.v("jitters", String.valueOf(jitters));
        Log.v("shimmers", String.valueOf(shimmers));
        Log.v("jittersSize", String.valueOf(jitters.size()));
        Log.v("shimmersSize", String.valueOf(shimmers.size()));
    }
    private static List<Integer> getPeriods(List<Integer> extremePos){
        List<Integer> T = new ArrayList<Integer>();
        for(int i = 0; i < extremePos.size() - 1; i ++){
            // calculate each period and add it into list
            T.add(extremePos.get(i+1) - extremePos.get(i));
        }
        return T;
    }

    private static List<Integer> getPitchPositions(int start, int end){
        short pitchInit = 0;
        List<Integer> pitchPos = new ArrayList<>();

        for(int i = start; i < end; i ++){
            // initializer the pitch from the sample data
            if(pitchInit < data_processed.get(i)){
                pitchInit = data_processed.get(i);
            }
        }
        // set the threshold
        double threshold = pitchInit * RATIO_AMP_THRESHOLD;
        short maxAmp = 0;
        for(int i = start; i < end; i++){
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

    private static List<Integer> getTroughPositions(int start, int end){
        short troughInit = 0;
        List<Integer> troughPos = new ArrayList<>();
        for(int i = start; i < end; i ++){
            // initializer the trough from sample data
            if(troughInit > data_processed.get(i)){
                troughInit = data_processed.get(i);
            }
        }
        // set the threshold
        double threshold = troughInit * RATIO_AMP_THRESHOLD;
        short minAmp = 0;
        for(int i = start; i < end; i++){
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

    private static double getJitter(List<Integer> extremePos){
        List<Integer> T ;
        double T_diff_sum = 0.0;
        double T_sum = 0.0;
        double jitter = 0.0;

        try {
            if(extremePos.size() != 0) {
                T = getPeriods(extremePos);
                // get jitter
                for (int i = 0; i < T.size() - 1; i++) {
                    //Log.v("T", String.valueOf(T));
                    //Log.v("t(i)-t(i+1)", String.valueOf(Math.abs(T.get(i) - T.get(i+1))));
                    T_diff_sum += Math.abs(T.get(i) - T.get(i + 1));
                    T_sum += T.get(i);
                }
                // add the last period into sum
                if (T.size() > 0) {
                    T_sum += T.get(T.size() - 1);
                }
                // calculate jitter (relative)
                jitter = (T_diff_sum / (double) (T.size() - 1)) / (T_sum / (double) T.size()) * 100;
            }else{
                jitter = 0.0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jitter;
    }

    private static double selectJitter(double jitterPitch, double jitterTrough){
        double jitter = 0.0;
        double mean = (jitterPitch + jitterTrough) / 2;
        double variance = Math.sqrt((jitterTrough - mean)*(jitterTrough - mean) + (jitterPitch - mean)*(jitterPitch - mean));

        // invalid jitter results
        if(jitterPitch > 50 || jitterTrough > 50){
            selectedNum = MODE_ERROR;
            return  -1.0;
        }

        // select the smallest jitter as the correct one
        if(jitterPitch < jitterTrough){
            selectedNum = MODE_PITCH;
            jitter = jitterPitch;
        }
        else {
            selectedNum = MODE_TROUGH;
            jitter = jitterTrough;
        }

        if(variance < 1.0){
            jitter  =  mean;
        }
        return jitter;
    }

    public static double getJitter(){
        try {
            double sum = 0;
            jitter = 0;
            for(double j : jitters){
                sum += j;
            }
            // calculate average jitter
            jitter = sum/jitters.size();

             return jitter;
        }catch (Exception e){
            e.printStackTrace();
            return -1.0;
        }
    }


    private static double getShimmer(ArrayList<Integer> ampPk2Pk){
        long A_diff_sum = 0;
        long A_sum = 0;
        for(int i = 0; i < ampPk2Pk.size() - 1; i++){
            A_diff_sum += Math.abs(ampPk2Pk.get(i) - ampPk2Pk.get(i+1));
            A_sum += ampPk2Pk.get(i);
        }
        // add the last peak-to-peak amplitude into sum
        if(ampPk2Pk.size() > 0) {
            A_sum += ampPk2Pk.get(ampPk2Pk.size() - 1);
        }
        // calculate shimmer (relative)
        return ((double) A_diff_sum / (double) (ampPk2Pk.size() - 1)) / ((double) A_sum / (double) ampPk2Pk.size()) * 100;
    }

    public static double getShimmer(){
        try {
            double sum = 0;
            shimmer = 0;
            for(double j : shimmers){
                sum += j;
            }
            // calculate average jitter
            shimmer = sum/shimmers.size();

            return shimmer;
        }catch (Exception e){
            e.printStackTrace();
            return -1.0;
        }
    }

    public static double getF0(){
        double sum = 0.0;
        for(double t : periods){
            sum += t;
        }
        Log.v("sum",String.valueOf(sum));

        double averageT = sum / periods.size() / 44100;
        Log.v("averageT",String.valueOf(averageT));
        f0 = 1 / averageT ;
        return f0;
    }

}
