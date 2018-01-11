package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/01/2018.
 */

public class FeaturesCalculation {
    final private int BASE_FRAGMENT = 180;
    final private int OFFSET = 90;
    private List<Integer> pitchPositions = new ArrayList<>();
    private List<Integer> T = new ArrayList<>();
    List<Short> data;

    public FeaturesCalculation(List<Short> data){
        this.data = data;
        calculatePeriods();
    }

    private void calculatePeriods(){
        int size = data.size();
        int maxAmp = 0;
        int startPos = 0;
        // get the first pitch in the basic period
        for (int i = 0; i < BASE_FRAGMENT; i ++){
            if (maxAmp < data.get(i)){
                maxAmp = data.get(i);
                // set this position as the start position
                startPos = i;
            }
        }
        Log.v("startPos", String.valueOf(startPos));
        // find every pitch in all the fragments
        int pos = startPos + OFFSET; // set current position
        int posAmpMax;
        while(startPos < size - BASE_FRAGMENT){
            if(data.get(pos) > 0) { // only read the positive data
                posAmpMax = 0;
                maxAmp = 0;
                // access to all the data in this fragment
                while (pos < startPos + BASE_FRAGMENT) {
                    // find the pitch and mark this position
                    if (maxAmp < data.get(pos)) {
                        maxAmp = data.get(pos);
                        posAmpMax = pos;
                    }
                    pos++;
                }
                // add pitch position into the list
                pitchPositions.add(posAmpMax);
                // update the start position and the current position
                startPos = posAmpMax;
                pos =  startPos + OFFSET;
            }else{
                pos ++;
            }
        }

        // calculate all periods and add them into list
        for(int i = 0; i < pitchPositions.size() - 1; i++){
            T.add(pitchPositions.get(i+1) - pitchPositions.get(i));
        }
    }

    // FEATURE NUMBER 1 : SHIMMER
    public double getShimmer(){
        int minAmp = 0;
        int maxAmp;
        long A_diff_sum = 0; // sum of difference between every two peak-to-peak amplitudes
        long A_sum = 0; // sum of all the peak-to-peak amplitudes
        List<Integer> ampPk2Pk = new ArrayList<>(); // this list contains all the peak-to-peak amplitudes

        for(int i = 0; i < pitchPositions.size() - 1; i ++){
            // get each pitch
            maxAmp = data.get(pitchPositions.get(i));
            for(int j = pitchPositions.get(i); j < pitchPositions.get(i + 1); j ++){
                if(minAmp > data.get(j)){
                    minAmp = data.get(j);
                }
            }
            // add peak-to-peak amplitude into the list
            ampPk2Pk.add(maxAmp - minAmp);
            // reset the min amplitude
            minAmp = 0;
        }

        // SHIMMER FORMULA (RELATIVE)
        for (int i = 0; i < ampPk2Pk.size() - 1; i++) {
            A_diff_sum += Math.abs(ampPk2Pk.get(i) - ampPk2Pk.get(i + 1));
            A_sum += ampPk2Pk.get(i);
        }
        // add the last peak-to-peak amplitude into sum
        if (ampPk2Pk.size() > 0) {
            A_sum += ampPk2Pk.get(ampPk2Pk.size() - 1);
        }
        // calculate shimmer (relative)
        return ((double) A_diff_sum / (double) (ampPk2Pk.size() - 1)) / ((double) A_sum / (double) ampPk2Pk.size());
    }

    // FEATURE NUMBER 2 : JITTER
    public double getJitter(){
        double T_diff_sum = 0.0; // sum of difference between every two periods
        double T_sum = 0.0; // sum of all periods

        // JITTER FORMULA (RELATIVE)
        for (int i = 0; i < T.size() - 1; i++) {
            T_diff_sum += Math.abs(T.get(i) - T.get(i + 1));
            T_sum += T.get(i);
            Log.v("t+1 - t", String.valueOf(T.get(i+1) - T.get(i)));
        }

        // add the last period into sum
        if (T.size() > 0) {
            T_sum += T.get(T.size() - 1);
        }

        // calculate jitter (relative)
        return (T_diff_sum / (double) (T.size() - 1)) / (T_sum / (double) T.size());
    }

    // FEATURE NUMBER 2 : FUNDAMENTAL FREQUENCY
    public  double getF0(){
        double sum = 0.0; // sum of all the fundamental frequencies
        for(double t : T){
            sum += t;
        }
        // average period in second
        double averageT = sum / T.size() / 44100;
        Log.v("averageT",String.valueOf(averageT));
        // f = 1/T
        return 1 / averageT ;
    }
}
