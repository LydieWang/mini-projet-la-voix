package com.polytech.di.tianxue.voix_analyse;

import android.util.Log;

import org.jtransforms.fft.*;

import java.security.spec.ECField;

/**
 * Created by Administrator on 07/10/2017.
 */
public class FFT {
    private static final int FFT_SIZE = 4096;
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


    public double[] getFFT(short[] data){
        DoubleFFT_1D fft= new DoubleFFT_1D(FFT_SIZE);
        double [] doubleFFT = new double[FFT_SIZE];

        for (int i = 0; i < FFT_SIZE && i <= data.length; i++) {

            // transform short to double
            doubleFFT[i] = (double) data[i]/ 32768.0;
            //Log.d("double", String.valueOf(doubleFFT[i]));
        }

        fft.realForward(doubleFFT);
        return doubleFFT;
        /*
        Complex[] complexData = new Complex[audioData.length];
        for (int i = 0; i < complexData.length; i++) {
            complextData[i] = new Complex(audioData[i], 0);
        }
        Complex[] fftResult = FFT.fft(complexData);
        */
    }

    public double[] getAmplitude(double[] doubleFFT){
        int size = doubleFFT.length/2;
        double[] amplitude = new double[size];
        for(int i = 0; i <= size - 1; i ++){
            amplitude[i] = Math.sqrt(doubleFFT[2 * i] * doubleFFT[2 * i] + doubleFFT[2 * i + 1] * doubleFFT[2 * i + 1]);
            Log.d("amplitude", String.valueOf(amplitude[i]));
        }
        return amplitude;
    }
}