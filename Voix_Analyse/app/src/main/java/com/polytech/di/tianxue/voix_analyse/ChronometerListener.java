package com.polytech.di.tianxue.voix_analyse;

import android.os.SystemClock;
import android.widget.Chronometer;

/**
 * Created by Administrator on 02/10/2017.
 */

public class ChronometerListener implements Chronometer.OnChronometerTickListener {

    private long TIME_MAX = 5000;
    public void onChronometerTick(Chronometer chronometer) {

        /*
        if (SystemClock.elapsedRealtime() - chronometer.getBase() >= TIME_MAX) {
            chronometer.stop();
        }
        */
    }
}