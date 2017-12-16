package com.polytech.di.tianxue.voix_analyse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalyseActivity extends AppCompatActivity {
    private AudioPlayer audioPlayer = new AudioPlayer();
    private Button buttonPlay;
    private Button buttonPause;
    private Button buttonStop;
    private TextView textHint;
    private LinearLayout layout;


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

        buttonPlay.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);

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
        audioPlayer.destroy();
        super.onDestroy();
    }

    public void showWaves(View view){
        final DrawView drawView = new DrawView(this);
        drawView.setMinimumHeight(300);
        drawView.setMinimumWidth(500);
        //通知view组件重绘
        drawView.invalidate();
        layout.addView(drawView);

        //setView(drawDataView);
    }

    private void setView(View view){
        setContentView(view);
    }
}
