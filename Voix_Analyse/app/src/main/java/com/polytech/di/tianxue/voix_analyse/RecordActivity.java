package com.polytech.di.tianxue.voix_analyse;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private TextView textView_hint;
    private RelativeLayout layout;
    private Button button_start;
    private Button button_stop;
    private MediaRecorder mediaRecorder;
    private File dirSD;
    private boolean isCardMonted;
    private File recAudioFile;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        init();
    }

    private void init(){
        layout = (RelativeLayout)findViewById(R.id.content);
        button_start = (Button)findViewById(R.id.button_start);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_start.setEnabled(true);
        button_stop.setEnabled(false);
        isCardMonted = false;
    }

    public void startRecording(View view){
        textView_hint = new TextView(this);
        textView_hint.setTextSize(40);
        textView_hint.setText("RECORDING...");
        layout.addView(textView_hint);
        button_start.setEnabled(false);
        button_stop.setEnabled(true);

        isCardMonted = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if(isCardMonted) {
            dirSD = Environment.getExternalStorageDirectory();
            filePath = dirSD.getPath();
        }

        try {
            recAudioFile = new File(filePath, "Nouveau Enregistrement.wav");
            if (recAudioFile.exists()) {
                recAudioFile.delete();
            }
            mediaRecorder = new MediaRecorder();

            /* 设置录音来源为麦克风 */
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //文件保存位置
            mediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(View view){
        textView_hint.setVisibility(view.INVISIBLE);
        button_stop.setEnabled(false);

        if(recAudioFile != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }
}
