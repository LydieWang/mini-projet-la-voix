package com.polytech.di.tianxue.voix_analyse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Administrator on 08/12/2017.
 */

public class DrawFrequencyView extends View {
    public DrawFrequencyView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = 300;
        final int offsetX = 5;
        final int offsetY = canvasHeight;

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        // X
        canvas.drawLine(offsetX, offsetY, canvasWidth - offsetX, offsetY, paint1);
        // Y
        canvas.drawLine(offsetX, 0, offsetX, canvasHeight, paint1);


        double[] data_doubleFFT = FFT.getFFT(AudioData.data);
        double[] frequencies = FFT.getFrequencies(data_doubleFFT,44100);
        double[] amplitudes = FFT.getAmplitudes(data_doubleFFT);
        final double scaleY = (double) canvasHeight/FFT.maxAmplitude;
        final double scaleX = (double) canvasWidth/FFT.FFT_SIZE;

        final Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStrokeWidth(1);

        for(int i = 0; i < FFT.FFT_SIZE/2; i++){
            canvas.drawLine((float)(offsetX + i * scaleX*2), offsetY,
                    (float)(offsetX + i * scaleX*2), (float) (offsetY - amplitudes[i] * scaleY), paint2);
        }

    }
}
