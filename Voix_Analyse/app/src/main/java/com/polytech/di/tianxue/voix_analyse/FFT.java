package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import org.jtransforms.fft.*;

import java.util.List;


/**
 * Created by Administrator on 07/10/2017.
 */
public class FFT {
    public static final int FFT_N = 4096;

    /*
    public static double[] getFFT(List<Short> data){

        double [] data_doubleFFT = new double[FFT_N];

        try{
            if(data.size() < FFT_N) {
                throw new Exception("Error: length of FFT too small !");
            }else{
                // set offset
                int offset = data.size() / FFT_N;
                int i = 0, j =0;
                while(i < FFT_N){
                    data_doubleFFT[i] = (double) data.get(j) / 32768.0;
                    i ++;
                    j += offset;
                }
            }
        }catch (Exception e){
            Log.e("FFT size : " , e.getMessage());
            return null;
        }
        // FFT
        DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(FFT_N);
        doubleFFT_1D.realForward(data_doubleFFT);
        return data_doubleFFT;
    }
    */

    /*
    public static double[] getAmplitudes(double[] data_doubleFFT){
        try {
            if (data_doubleFFT.length % 2 != 0) { // data size should be even
                throw new Exception("Error: length of FFT illegal !");
            } else {
                int size = FFT_N/ 2;
                // the array for storing the amplitudes
                double[] amplitudes = new double[size];
                for (int i = 0; i < size; i++) {
                    // calculate the amplitude of each frequency = sqrt(real[i] * real[i] + img[i] * img[i])
                    amplitudes[i] = Math.sqrt(data_doubleFFT[2 * i] * data_doubleFFT[2 * i] + data_doubleFFT[2 * i + 1] * data_doubleFFT[2 * i + 1]);
                    // get the highest frequency
                    if(amplitudes[i] > AudioData.maxAmplitudeFre){
                        AudioData.maxAmplitudeFre = amplitudes[i];
                    }
                }
                return amplitudes;
            }
        }catch (Exception e){
            Log.e("FFT size" , e.getMessage());
            return null;
        }
    }
    */
}