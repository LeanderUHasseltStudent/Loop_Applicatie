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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__loop_data);

        textViewName = (TextView)findViewById(R.id.name);
        textViewDistance = (TextView)findViewById(R.id.distance);
        textViewTime = (TextView)findViewById(R.id.time);
        textViewVelocity = (TextView)findViewById(R.id.velocity);

        Intent intent = getIntent();
        if(intent.hasExtra("Intent.EXTRA_TEXT1")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT1");
            textViewName.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT2")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT2");
            textViewDistance.setText(text);
        }
        if(intent.hasExtra("Intent.EXTRA_TEXT3")){
            String text = intent.getStringExtra("Intent.EXTRA_TEXT3");
            textViewTime.setText(text);
        }
    }
}
