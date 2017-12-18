package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import org.jtransforms.fft.*;

import java.util.List;


/**
 * Created by Administrator on 07/10/2017.
 */
public class FFT {
    public static final int FFT_N = 4096;

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

    /*
    public static double[] getFrequencies(double[] data_doubleFFT, int sampleRate){
        try {
            if (data_doubleFFT.length % 2 != 0) { // data size should be even
                throw new Exception("Error: length of FFT illegal !");
            } else {
                int size = data_doubleFFT.length;
                double[] frequencies = new double[size/2];
                for (int i = 0; i < size/2; i++) {
                    // the frequencies
                    frequencies[i] = i * sampleRate / size;
                }
                return frequencies;
            }
        }catch (Exception e){
            Log.e("FFT size" , e.getMessage());
            return null;
        }
    }
    */

    /*
    public static final int FFT_N = 4096;
    public static final int SAMPLE_RATE = 44100; //Hz

    public ComplexNumber[] getFFT(ComplexNumber[] data){
        int N = data.length;
        if(N == 1){
            return new ComplexNumber[]{data[0]};
        }else if(N % 2 != 0){
            throw new RuntimeException("N is not a power of 2");
        }else {
            //FFT of even/odd terms
            ComplexNumber[] even = new ComplexNumber[N / 2];
            ComplexNumber[] odd = new ComplexNumber[N / 2];
            for (int k = 0; k < N / 2; k++) {
                even[k] = data[2 * k];
                odd[k] = data[2 * k + 1];
            }

            ComplexNumber[] q = getFFT(even);
            ComplexNumber[] r = getFFT(odd);

            ComplexNumber[] y = new ComplexNumber[N];
            for (int k = 0; k < N / 2; k++) {
                double kth = -2 * k * Math.PI / N;
                ComplexNumber wk = new ComplexNumber(Math.cos(kth), Math.sin(kth));
                y[k] = q[k].add(wk.multiply(r[k]));
                y[k + N / 2] = q[k].minus(wk.multiply(r[k]));
            }

            return y;
        }
    }

    public ComplexNumber[] getFrequencyComplex(short[] data){
        int lengthData = data.length;
        try{
            // change the type of data to complex number
            ComplexNumber[]  data_complex = new ComplexNumber[lengthData];
            for(int i = 0; i < lengthData; i++){
                data_complex[i] = new ComplexNumber(data[i],0);
            }

            ComplexNumber[] frequency_complex = new ComplexNumber[lengthData];
            frequency_complex = getFFT(data_complex);

            for(int i = 0; i < lengthData; i++){
                //Log.v("Frequency Complex Real", String.valueOf(frequency_complex[i].getMod()));
                //Log.v("Frequency Complex Real", String.valueOf(frequency_complex[i].getReal()));
                //Log.v("Frequency Complex Imag", String.valueOf(frequency_complex[i].getImaginary()));
                Log.v("Frequency", String.valueOf(frequency_complex[i].getReal()));
            }
            return frequency_complex;

        }catch (Exception e){
            Log.e("FFT",e.getMessage());
            return new ComplexNumber[0];
        }
    }
*/
    /*
    public static double GetFrequency(short[] data){
        if(data.length < FFT_N){
            throw new RuntimeException("Data length lower than " + FFT_N);
        }
        ComplexNumber[]  f = new ComplexNumber[FFT_N];
        for(int i=0;i<FFT_N;i++){
            f[i] = new ComplexNumber(data[i],0); //实部为正弦波FFT_N点采样，赋值为1
            //虚部为0
        }

        f = getFFT(f);

        double[]  s = new double[FFT_N/2];
        for(int i=0; i<FFT_N/2; i++){
            s[i] = f[i].getMod();
        }

        int fmax=0;
        for(int i=1;i<FFT_N/2;i++){  //利用FFT的对称性，只取前一半进行处理
            if(s[i]>s[fmax])
                fmax=i;                          //计算最大频率的序号值
        }
        double fre = fmax*(double)SAMPLE_RATE / FFT_N;
        return fre;
    }
    */
}