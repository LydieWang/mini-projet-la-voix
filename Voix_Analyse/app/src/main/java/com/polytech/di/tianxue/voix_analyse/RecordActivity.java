package com.polytech.di.tianxue.voix_analyse;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    private TextView textView_recording;
    private TextView textView_hint;
    private Button button_start;
    private Button button_stop;
    private Button button_analyse;
    private AudioCapturer audioCapturer;
    private Chronometer chronometer = null;
    private static final int STAT_START_RECORD = 0;
    private static final int STAT_STOP_RECORD = 1;
    private RecordThread recordThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // initialize members so we can manipulate them later
        button_start = (Button)findViewById(R.id.button_start);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_analyse = (Button)findViewById(R.id.button_analyse);
        chronometer  = (Chronometer) findViewById(R.id.chronometer);
        textView_recording = (TextView) findViewById(R.id.text_recording);
        textView_hint = (TextView) findViewById(R.id.text_hint);

        button_start.setEnabled(true);
        button_stop.setEnabled(false);
        button_analyse.setEnabled(false);
        chronometer.setVisibility(View.INVISIBLE);
        textView_recording.setVisibility(View.INVISIBLE);
    }

    private void setView(int status){
        if(status == STAT_START_RECORD){
            button_start.setEnabled(false);
            button_stop.setEnabled(true);
            chronometer.setVisibility(View.VISIBLE);
            textView_recording.setVisibility(View.VISIBLE);
            textView_hint.setVisibility(View.INVISIBLE);

        }else if(status == STAT_STOP_RECORD){
            button_stop.setEnabled(false);
            button_analyse.setEnabled(true);
            textView_recording.setText(getText(R.string.text_finish));

        }else{
            return;
        }
    }

    private void initChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setFormat("%s");
    }

    public void startRecording(final View view){

        setView(STAT_START_RECORD);

        // initialise and start the chronometer
        initChronometer();
        chronometer.start();

        // start a new thread of RecordThread
        recordThread = new RecordThread(audioCapturer);
        Thread thread = new Thread(recordThread);
        thread.start();
        // recording time <= 5 seconds
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(SystemClock.elapsedRealtime() - chronometer.getBase() >= 5000)
                {
                    stopRecording(view);
                }
            }
        });
    }

    public void stopRecording(View view){
        setView(STAT_STOP_RECORD);
        chronometer.stop();
        recordThread.stop();
    }

    public void seeTheResult(View view)  {
        // start a new activity
        Intent intent = new Intent(this, AnalysisActivity.class);
        startActivity(intent);
    }

}
