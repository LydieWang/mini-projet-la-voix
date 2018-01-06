package com.polytech.di.tianxue.voix_analyse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalysisActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer = new AudioPlayer();
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonStop;
    private Button buttonShowWaves;
    private Button buttonShowShimmer;
    private Button buttonShowJitter;
    private TextView textHint;
    private LinearLayout layout;
    private ProgressDialog progressDialog;
    private TextView textShimmer;
    private TextView textJitter;
    private double shimmer;
    private double jitter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */

        init();

    }

    private void init(){
        buttonPlay = (Button)findViewById(R.id.button_play_audio);
        buttonPause = (Button)findViewById(R.id.button_pause_audio);
        buttonStop = (Button)findViewById(R.id.button_stop_audio);
        textHint = (TextView)findViewById(R.id.text_hint_play);
        buttonShowWaves = (Button)findViewById(R.id.button_wave_freq);
        buttonShowShimmer = (Button)findViewById(R.id.button_shimmer);
        buttonShowJitter = (Button)findViewById(R.id.button_jitter);

        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonShowWaves.setEnabled(true);
        buttonShowShimmer.setEnabled(true);
        buttonShowJitter.setEnabled(true);
        
        layout = (LinearLayout) findViewById(R.id.layout_analyse);
    }


    public void playAudio(View view){
        buttonPlay.setEnabled(false);
        buttonPause.setEnabled(true);
        buttonStop.setEnabled(true);
        textHint.setText(getText(R.string.text_play));
        audioPlayer.play();
    }

    public void pauseAudio(View view){
        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(true);
        textHint.setText(getText(R.string.text_pause));
        audioPlayer.pause();
    }

    public void stopAudio(View view){
        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        textHint.setText(getText(R.string.text_stop));
        audioPlayer.stop();
    }

    protected  void onDestroy(){
        super.onDestroy();
        audioPlayer.destroy();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    Handler handlerBtnWave = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            final DrawView drawView = new DrawView(AnalysisActivity.this);
            layout.addView(drawView);

            //close the progressDialog
            progressDialog.dismiss();
        }
    };

    public void showWaves(View view){
        // a progressDialog which shows the information
        progressDialog = ProgressDialog.show(this,"Drawing the waves","Please wait for a moment ...");

        new Thread(new Runnable() {// start a new Thread for calculating until it finishes
            @Override
            public void run() {
                // the functions that cost a lot of time
                AudioData.getAmplitudesFre();
                AudioData.getMaxAmplitudeAbs();
                // send message to handler
                handlerBtnWave.sendEmptyMessage(0);
            }
        }).start();

        buttonShowWaves.setEnabled(false);
    }

    Handler handlerBtnShimmer = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //close the progressDialog
            progressDialog.dismiss();
            textShimmer = new TextView(AnalysisActivity.this);
            textShimmer.setText("Shimmer : "+ shimmer + "%");
            layout.addView(textShimmer);
            //disable the button after using it
            buttonShowShimmer.setEnabled(false);
        }
    };

    public void showShimmer(View view){
        progressDialog = ProgressDialog.show(this,"Calculating the shimmer","Please wait for a moment ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                shimmer = AudioData.getShimmer();
                handlerBtnShimmer.sendEmptyMessage(0);
            }
        }).start();
    }

    Handler handlerBtnJitter = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //close the progressDialog
            progressDialog.dismiss();
            textJitter = new TextView(AnalysisActivity.this);
            textJitter.setText("Jitter : "+ jitter + "%");
            layout.addView(textJitter);
            //disable the button after using it
            buttonShowJitter.setEnabled(false);
        }
    };

    public void showJitter(View view){
        progressDialog = ProgressDialog.show(this,"Calculating the jitter","Please wait for a moment ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                jitter = AudioData.getJitter();
                handlerBtnJitter.sendEmptyMessage(0);
            }
        }).start();
    }
}
