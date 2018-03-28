package com.example.vanca.loop_application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_Loop_Applicatie extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private RecyclerView mList;
    private Cursor mCursor;
    private TextView loopDataTextView;
    private Button button;
    private MyRecyclerViewAdapter adapter;

    public MainActivity_Loop_Applicatie() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__loop__applicatie);

        // data to populate the RecyclerView with
        ArrayList<String> locaties = new ArrayList<>();
        locaties.add("Locatie1");
        locaties.add("Locatie2");
        locaties.add("Locatie3");
        locaties.add("Locatie4");
        locaties.add("Locatie5");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, locaties, this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
