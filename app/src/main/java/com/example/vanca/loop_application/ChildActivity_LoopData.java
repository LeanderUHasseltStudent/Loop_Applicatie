package com.example.vanca.loop_application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

public class ChildActivity_LoopData extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private LocationManagment locationManagment;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__loop_data);
        textView = (TextView)findViewById(R.id.locaties);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                locationManagment = new LocationManagment(getApplicationContext());
                locationManagment.setUp();
                locationManagment.startTracking();
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                locationManagment.stopTracking();
                textView.setText("lol");
            }
        });
    }
}
