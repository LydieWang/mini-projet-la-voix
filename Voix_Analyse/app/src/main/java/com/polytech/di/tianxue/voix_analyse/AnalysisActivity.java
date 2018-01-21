package com.polytech.di.tianxue.voix_analyse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;

/**
 * @author      Tianxue WANG and Wenli YAN
 * @version     2018.0115
 * @date        07/12/2017
 */

/**
 * The class of the activity of analysis
 */
public class AnalysisActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer = new AudioPlayer();
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonStop;
    private Button buttonShowWaves;
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
    private final double SHIMMER_LIMIT = 0.03;
    private final double JITTER_LIMIT = 0.01;
    private final double SHIMMER_LIMIT_ERROR = 0.2;
    private final double JITTER_LIMIT_ERROR = 0.2;

    /**
     * The method overridden
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        init();
        analyseData();
    }

    /**
     * The method initialising the view of this activity
     */
    private void init(){
        buttonPlay = (Button)findViewById(R.id.button_play_audio);
        buttonPause = (Button)findViewById(R.id.button_pause_audio);
        buttonStop = (Button)findViewById(R.id.button_stop_audio);
        textHint = (TextView)findViewById(R.id.text_hint_play);
        buttonShowWaves = (Button)findViewById(R.id.button_wave_freq);

        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonShowWaves.setEnabled(false);

        layout = (LinearLayout) findViewById(R.id.layout_analyse);
    }

    /**
     * The button for playing the sound
     * @param view the view
     */
    public void playAudio(View view){
        buttonPlay.setEnabled(false);
        buttonPause.setEnabled(true);
        buttonStop.setEnabled(true);
        textHint.setText(getText(R.string.text_play));
        audioPlayer.play();
    }

    /**
     * The button for pausing the sound
     * @param view the view
     */
    public void pauseAudio(View view){
        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(true);
        textHint.setText(getText(R.string.text_pause));
        audioPlayer.pause();
    }

    /**
     * The button for stopping the sound
     * @param view the view
     */
    public void stopAudio(View view){
        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        textHint.setText(getText(R.string.text_stop));
        audioPlayer.stop();
    }

    /**
     * The method releasing the resources
     */
    protected  void onDestroy(){
        super.onDestroy();
        audioPlayer.destroy();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * The button for showing the diagrams
     * @param view the view
     */
    public void showWaves(View view){
        DrawView drawView = new DrawView(AnalysisActivity.this);
        layout.addView(drawView);

        buttonShowWaves.setEnabled(false);
    }

    /**
     * The button for going back to the first activity
     * @param view the view
     */
    public void goBack(View view){
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
        audioPlayer.stop();
    }

    Handler handlerBtnProcessData = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            DecimalFormat df = new DecimalFormat("0.00");

            try{
                //close the progressDialog
                progressDialog.dismiss();
                buttonShowWaves.setEnabled(true);

                // show shimmer
                textShimmer = new TextView(AnalysisActivity.this);
                textShimmer.setText("   Shimmer : "+ df.format(shimmer * 100) + "%");
                layout.addView(textShimmer);

                // show jitter
                textJitter = new TextView(AnalysisActivity.this);
                textJitter.setText("    Jitter : "+ df.format(jitter * 100) + "%");
                layout.addView(textJitter);

                // show f0
                textF0 = new TextView(AnalysisActivity.this);
                textF0.setText("    F0 : "+ df.format(f0) + "Hz");
                layout.addView(textF0);

                // show result
                testResult = new TextView(AnalysisActivity.this);

                if(shimmer > SHIMMER_LIMIT_ERROR && jitter > JITTER_LIMIT_ERROR){
                    testResult.setText("    Please test your voice again because there is some problem with the data !");
                }

                if(shimmer < SHIMMER_LIMIT && jitter < JITTER_LIMIT){
                    testResult.setText("    Your voice is good. No problem !");
                }else
                    testResult.setText("    Your voice is not perfect. You'd better see a doctor.");

                layout.addView(testResult);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    };

    /**
     * The method analysing the audio data
     */
    public void analyseData() {
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
