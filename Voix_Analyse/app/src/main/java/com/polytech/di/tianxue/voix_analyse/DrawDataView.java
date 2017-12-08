package com.polytech.di.tianxue.voix_analyse;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 07/12/2017.
 */

public class DrawDataView extends View {
    public DrawDataView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = 300;
        final int offsetX = 5;
        final int offsetY = canvasHeight/2;
        final short maxA = AudioData.maxAmplitude;
        final short minA_abs = (short) Math.abs(AudioData.minAmplitude);
        final long dataLength = AudioData.length;

        int height;
        if(maxA > minA_abs){
            height = maxA *  2;
        }else{
            height = minA_abs * 2;
        }

        final double scaleY = (double) canvasHeight/height;
        final double scaleX = (double) canvasWidth/AudioData.length;

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        // X
        canvas.drawLine(offsetX, offsetY, canvasWidth - offsetX, offsetY, paint1);
        // Y
        canvas.drawLine(offsetX, 0, offsetX, canvasHeight, paint1);

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

    }
}
