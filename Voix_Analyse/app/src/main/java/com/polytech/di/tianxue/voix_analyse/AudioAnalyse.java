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

    /*
    public void readFile() throws Exception {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            throw(new Exception("SDCard not found ! "));
        }
        File file = new File(Environment.getExternalStorageDirectory(),
                "New Record.wav");
        FileInputStream fis = new FileInputStream(file);
        byte[] b = new byte[inputStream.available()];
        fis.read(b);
        String result = new String(b);
        System.out.println("读取成功："+result);

    }
*/

}
