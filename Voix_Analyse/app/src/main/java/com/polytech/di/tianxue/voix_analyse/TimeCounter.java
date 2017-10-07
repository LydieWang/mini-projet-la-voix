package com.polytech.di.tianxue.voix_analyse;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 02/10/2017.
 */

public class TimeCounter {
    private long seconds;
    private long milSeconds;
    private Timer timer;
    private TimerTask timerTask;
    private long TIME_DELAY = 0l;
    private long TIME_PERIOD = 10l;
    private long TIME_LIMIT = 99l;

    public void TimeCounter(){
        seconds = 0l;
        milSeconds = 0l;

        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                milSeconds ++;
                if(milSeconds >= TIME_LIMIT){
                    seconds ++;
                    milSeconds = 0l;
                }
            }
        };
    }

    public long getSeconds(){
        return seconds;
    }

    public long getMilSeconds(){
        return milSeconds;
    }

    public void startTimeCounter(){
        timer.schedule(timerTask, TIME_DELAY, TIME_PERIOD);
    }

    public void stopTimeCounter(){
        timer.cancel();
        timerTask.cancel();

    }
}
