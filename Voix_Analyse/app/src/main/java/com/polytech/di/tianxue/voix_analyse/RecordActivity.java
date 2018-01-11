package com.polytech.di.tianxue.voix_analyse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class RecordActivity extends AppCompatActivity {

    private TextView textView_recording;
    private TextView textView_hint;
    private Button button_start;
    private Button button_stop;
    private Button button_analyse;
    private Button button_restart;

    private Chronometer chronometer = null;
    private static final int STAT_START_RECORD = 0;
    private static final int STAT_STOP_RECORD = 1;
    private RecordThread recordThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

       initView();
    }

    private void initView(){
        // initialize members so we can manipulate them later
        button_start = (Button)findViewById(R.id.button_start);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_restart = (Button)findViewById(R.id.button_restart);
        button_analyse = (Button)findViewById(R.id.button_analyse);
        chronometer  = (Chronometer) findViewById(R.id.chronometer);
        textView_recording = (TextView) findViewById(R.id.text_recording);
        textView_hint = (TextView) findViewById(R.id.text_hint);


        button_start.setEnabled(true);
        button_stop.setEnabled(false);
        button_analyse.setEnabled(false);
        button_restart.setEnabled(false);
        chronometer.setVisibility(View.INVISIBLE);
        textView_recording.setVisibility(View.INVISIBLE);
        textView_recording.setText(getText(R.string.text_record));
        textView_hint.setVisibility(View.VISIBLE);

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
            button_restart.setEnabled(true);

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
        recordThread = new RecordThread();
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

    public void restart(View view)  {
        initView();
    }

    public class RecordThread implements Runnable{
        private AudioCapturer audioCapturer;
        @Override
        public void run() {
            // start capturing the audio
            audioCapturer = new AudioCapturer();
            audioCapturer.startCapture();
        }

        public void stop(){
            // stop capturing the audio
            audioCapturer.stopCapture();
        }
    }


    // test with the data in the SD card
    /********************************/
    private ProgressDialog progressDialog;
    Handler handlerBtnProcessData = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            try{
                //close the progressDialog
                progressDialog.dismiss();

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    };
     public void test(View view){
         progressDialog = ProgressDialog.show(this,"Analysing audio data","Please wait for a moment ...");

         new Thread(new Runnable() {
             @Override
             public void run() {

                 File file = new File(Environment.getExternalStorageDirectory(), "New Record.wav");

                 try {
                     FileInputStream inputStream = new FileInputStream(file);
                     byte[] b = new byte[inputStream.available()];
                     inputStream.read(b);
                     short[] s = new short[(b.length-44)/2];
                     ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(s);
                     List<Short> sl = new ArrayList<>();
                     for(short ss : s){
                        sl.add(ss);
                     }

                     List<Short> sl2 = new ArrayList<>();
                     for(int i = (int)(sl.size() * AudioData.RATIO_BEGINNING); i < sl.size() * AudioData.RATIO_ENDING; i ++){
                         sl2.add(sl.get(i));
                     }

                     FeaturesCalculation featuresCalculation = new FeaturesCalculation(sl2);
                     double shimmer = featuresCalculation.getShimmer();
                     double jitter = featuresCalculation.getJitter();
                     double f0 = featuresCalculation.getF0();
                     Log.v("shimmer", String.valueOf(shimmer * 100));
                     Log.v("jitter", String.valueOf(jitter * 100));
                     Log.v("f0", String.valueOf(f0));

                     handlerBtnProcessData.sendEmptyMessage(0);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
         }).start();
     }
     /*******************************/

}
