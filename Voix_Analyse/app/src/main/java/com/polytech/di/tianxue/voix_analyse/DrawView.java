package com.polytech.di.tianxue.voix_analyse;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 07/12/2017.
 */

public class DrawView extends View {
    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = 600;

        /*******************************************************************************/
        /* Draw Data */
        final int offsetX = 5;
        final int offsetY = canvasHeight/4;
        final short maxA = AudioData.maxAmplitude;
        final short minA_abs = (short) Math.abs(AudioData.minAmplitude);
        final long dataLength = AudioData.length;

        int height;
        if(maxA > minA_abs){
            height = maxA *  2;
        }else{
            height = minA_abs * 2;
        }

        final double scaleY = (double) canvasHeight/2/height;
        final double scaleX = (double) canvasWidth/AudioData.length;

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        // X
        canvas.drawLine(offsetX, offsetY, canvasWidth - offsetX, offsetY, paint1);
        // Y
        canvas.drawLine(offsetX, 0, offsetX, canvasHeight/2, paint1);

        final Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStrokeWidth(2);


        Path path = new Path();
        //path.moveTo((float) (0 * scaleX + offsetX),(float)(offsetY - AudioData.data.get(0)* scaleY));
        for (int i = 10; i < dataLength; i += 10){
            canvas.drawPoint((float) (offsetX + i * scaleX),(float)(offsetY - AudioData.data.get(i)* scaleY),paint2);
            //path.lineTo((float) (offsetX + i * scaleX),(float)(offsetY - AudioData.data.get(i)* scaleY));
        }
        //canvas.drawPath(path, paint2);


        // draw a line to separate two parts
        canvas.drawLine(0, canvasHeight/2, canvasWidth, canvasHeight/2, paint1);

        /*******************************************************************************/
        /* Draw Frequencies*/
        final int offsetY2 = canvasHeight;

        // X
        canvas.drawLine(offsetX, offsetY2, canvasWidth - offsetX, offsetY2, paint1);
        // Y
        canvas.drawLine(offsetX, offsetY2/2, offsetX, offsetY2, paint1);


        double[] data_doubleFFT = FFT.getFFT(AudioData.data);
        //double[] frequencies = FFT.getFrequencies(data_doubleFFT,44100);
        double[] amplitudes = FFT.getAmplitudes(data_doubleFFT);
        final double scaleY2 = (double) canvasHeight / 2/ FFT.maxAmplitude;
        final double scaleX2 = (double) canvasWidth/FFT.FFT_N;

        for(int i = 0; i < FFT.FFT_N/2; i++){
            canvas.drawLine((float)(offsetX + i * scaleX2 *2 ), offsetY2,
                    (float)(offsetX + i * scaleX2 * 2), (float) (offsetY2 - amplitudes[i] * scaleY2), paint2);
        }
    }
}
