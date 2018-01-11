package com.polytech.di.tianxue.voix_analyse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;


public class AnalysisActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer = new AudioPlayer();
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonStop;
    private Button buttonShowWaves;
    private Button buttonProcessData;
    private TextView textHint;
    private LinearLayout layout;
    private ProgressDialog progressDialog;
    private TextView textShimmer;
    private TextView textJitter;
    private TextView textF0;
    private TextView testResult;
    private double shimmer;
    private double jitter;
    private double f0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        init();
    }

    private void init(){
        buttonPlay = (Button)findViewById(R.id.button_play_audio);
        buttonPause = (Button)findViewById(R.id.button_pause_audio);
        buttonStop = (Button)findViewById(R.id.button_stop_audio);
        textHint = (TextView)findViewById(R.id.text_hint_play);
        buttonShowWaves = (Button)findViewById(R.id.button_wave_freq);
        buttonProcessData = (Button)findViewById(R.id.button_analyse_audio);

        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonShowWaves.setEnabled(false);
        buttonProcessData.setEnabled(true);

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
                //AudioData.getAmplitudesFre();
                AudioData.getMaxAmplitudeAbs();
                // send message to handler
                handlerBtnWave.sendEmptyMessage(0);
            }
        }).start();

        buttonShowWaves.setEnabled(false);
    }

    Handler handlerBtnProcessData = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            DecimalFormat df = new DecimalFormat("0.00");

            try{
                //close the progressDialog
                progressDialog.dismiss();
                buttonProcessData.setEnabled(false);
                buttonShowWaves.setEnabled(true);

                // show shimmer
                textShimmer = new TextView(AnalysisActivity.this);
                textShimmer.setText("Shimmer : "+ df.format(shimmer * 100) + "%");
                layout.addView(textShimmer);

                // show jitter
                textJitter = new TextView(AnalysisActivity.this);
                textJitter.setText("Jitter : "+ df.format(jitter * 100) + "%");
                layout.addView(textJitter);

                // show f0
                textF0 = new TextView(AnalysisActivity.this);
                textF0.setText("F0 : "+ df.format(f0) + "Hz");
                layout.addView(textF0);

                // show result
                testResult = new TextView(AnalysisActivity.this);
                if(shimmer < 3.0 && jitter < 1.0){
                    testResult.setText("Your voice is good. No problem !");
                }else
                    testResult.setText("Your voice is not perfect. You'd better see a doctor.");

                layout.addView(testResult);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    };

    public void analyseData(View view) {
        progressDialog = ProgressDialog.show(this,"Analysing audio data","Please wait for a moment ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioData.processData();
                FeaturesCalculation featuresCalculation = new FeaturesCalculation(AudioData.data_processed);

                shimmer = featuresCalculation.getShimmer();
                jitter = featuresCalculation.getJitter();
                f0 = featuresCalculation.getF0();

                handlerBtnProcessData.sendEmptyMessage(0);
            }
        }).start();
    }

}
