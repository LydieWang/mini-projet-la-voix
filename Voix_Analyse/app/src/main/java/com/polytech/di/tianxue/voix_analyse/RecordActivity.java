package com.polytech.di.tianxue.voix_analyse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }

    public void startRecording(View view){
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("STARTING");
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.content);
        layout.addView(textView);
        Button button = (Button)findViewById(R.id.button_record);
        button.setVisibility(view.INVISIBLE);
    }
}
