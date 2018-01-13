package com.polytech.di.tianxue.voix_analyse;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/**
 * Created by Administrator on 07/12/2017.
 */

public class DrawView extends View {
    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int canvasWidth = canvas.getWidth()-90;///
        final int canvasHeight = 600;
        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStrokeWidth(2);
        paint1.setTextSize(20);
        Paint paintBack = new Paint();
        paintBack.setColor(Color.LTGRAY);




        /*******************************************************************************/
        /* Draw Data */
        final int offsetX = 50;//
        final int offsetY = canvasHeight / 2;

        // final long dataLength = AudioData.length;
        final long dataLength = AudioData.data.size();
        int height = AudioData.maxAmplitudeAbs * 2;

        final double scaleY = (double) canvasHeight / 1.5 / height;
        final double scaleX = (double) ( canvasWidth-30 ) /dataLength;

        // Background
        canvas.drawRect(50,50, canvasWidth + 60,canvasHeight-50, paintBack);

        // Scale
        final double s = (canvasHeight-100)/ 4;
        for (int i = offsetY; i <= canvasHeight -50; i += s) {

            canvas.drawLine( offsetX, i, offsetX + 10, i, paint1);

        }
        for (int i = offsetY; i >= 50; i -= s) {

            canvas.drawLine( offsetX, i, offsetX + 10, i, paint1);
        }

        double indice = -1;
        for (int i = canvasHeight-50; i > 0; i -= s) {
            canvas.drawText(indice + " ",13, i, paint1);
            indice = indice + 0.5;
        }

        final Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStrokeWidth(2);

        //Paint paint3 = new Paint();
        //paint3.setColor(Color.RED);
        //Path path = new Path();
        //path.moveTo((float) (0 * scaleX + offsetX),(float)(offsetY - AudioData.data.get(0)* scaleY));
        for (int i = 10; i < dataLength; i += 10) {
            //path.lineTo((float) (offsetX + i * scaleX),(float)(offsetY - AudioData.data.get(i)* scaleY));
            canvas.drawLine((float) (offsetX + 20 + i * scaleX), (float) (offsetY - AudioData.data.get(i) * scaleY),
                    (float) (offsetX + 20 + i * scaleX), offsetY, paint2);
            //canvas.drawPoint((float) (offsetX + i * scaleX), (float) (offsetY - AudioData.data.get(i) * scaleY), paint2);
        }

        // X
        canvas.drawLine(offsetX, offsetY, canvasWidth + 60, offsetY, paint1);
        // Y
        canvas.drawLine(offsetX, 50, offsetX, canvasHeight-50, paint1);//


        //canvas.drawPath(path, paint3);

        // draw a line to separate two parts
        //canvas.drawLine(0, canvasHeight / 2, canvasWidth, canvasHeight / 2, paint1);

        /*******************************************************************************/
        /* Draw Frequencies*/
        /*
        final int offsetY2 = canvasHeight;

        // X
        canvas.drawLine(offsetX, offsetY2, canvasWidth - offsetX, offsetY2, paint1);
        // Y
        canvas.drawLine(offsetX, offsetY2 / 2, offsetX, offsetY2, paint1);

        final double scaleY2 = (double) canvasHeight / 2 / AudioData.maxAmplitudeFre;
        final double scaleX2 = (double) canvasWidth / (FFT.FFT_N / 2);

        for (int i = 0; i < FFT.FFT_N / 2; i++) {
            canvas.drawLine((float) (offsetX + i * scaleX2), offsetY2,
                    (float) (offsetX + i * scaleX2), (float) (offsetY2 - AudioData.amplitudesFre[i] * scaleY2), paint2);
        }
        */
    }

}
