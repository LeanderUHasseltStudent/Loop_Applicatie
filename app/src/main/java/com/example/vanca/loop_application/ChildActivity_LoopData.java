package com.example.vanca.loop_application;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

public class ChildActivity_LoopData extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewDistance;
    private TextView textViewTime;
    private TextView textViewVelocity;
    private TextView textViewMaxAltitude;
    private TextView textViewMinAltitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__loop_data);

        textViewDistance = (TextView)findViewById(R.id.distance);
        textViewTime = (TextView)findViewById(R.id.time);
        textViewVelocity = (TextView)findViewById(R.id.velocity);
        textViewMaxAltitude = (TextView)findViewById(R.id.maxAltitude);
        textViewMinAltitude = (TextView)findViewById(R.id.minAltitude);

        Intent intent = getIntent();
        if(intent.hasExtra("Intent.EXTRA_TEXT1")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT1");
            setTitle(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT2")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT2");
            textViewDistance.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT3")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT3");
            textViewTime.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT4")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT4");
            textViewVelocity.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT5")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT5");
            textViewMaxAltitude.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT6")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT6");
            textViewMinAltitude.setText(text);
        }
    }
}
