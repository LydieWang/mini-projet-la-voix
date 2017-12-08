package com.polytech.di.tianxue.voix_analyse;

/**
 * Created by Administrator on 14/10/2017.
 */
import android.os.Environment;
import android.util.Log;

import org.jtransforms.fft.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioAnalyse {
    private FileInputStream fis = null;
    private static final String FILE_NAME = "New Record.wav";


    public void readFile() throws Exception {
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            throw(new Exception("SDCard not found ! "));
        }
        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        if(!file.exists()){
            throw  new Exception ("File doesn't exist !");
        }
        fis = new FileInputStream(file);
        if(fis == null){
            throw  new Exception ("FileInputStream NULL !");
        }
    }

    public double getShimmer(){

        double shimmer = 0;
        int A_diff_sum = 0;
        int A_sum = 0;

        try {
            int length = fis.available();
            byte[] data = new byte[length];

            fis.read(data);
            for(int i = 0; i < length-1; i++){
                //Log.v("data[i]", String.valueOf(data[i]));
                A_diff_sum += Math.abs(data[i+1] - data[i]);
                A_sum += data[i];
            }
            A_sum += data[length-1];
            Log.v("A_diff_sum", String.valueOf(A_diff_sum));
            Log.v("A_sum", String.valueOf(A_sum));
            //shimmer = (double)( A_diff_sum/(length - 1) )/(double)(A_sum/length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shimmer;
    }

}
