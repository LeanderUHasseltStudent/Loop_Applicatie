package com.example.vanca.loop_application;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.location.Location;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity_Loop_Applicatie extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Cursor cursor;
    private MyRecyclerViewAdapter adapter;
    private SQLiteDatabase mDb;
    private ArrayList<Location> locations = new ArrayList<Location>();
    private MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
    private Button btn_start, btn_stop;
    public static final String BROADCAST_ACTION = "com.example.vanca.loop_application";
    private Date date1;
    private Date date2;
    SharedPreferences sharedPreferences;

    public MainActivity_Loop_Applicatie() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__loop__applicatie);

        LoopDataDBHelper dbHelper = new LoopDataDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        cursor = getAllSesions();
        setupSharedPreferences();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, cursor);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        btn_start = (Button) findViewById(R.id.startButton);
        btn_stop = (Button) findViewById(R.id.stopButton);
        btn_start.setBackgroundColor(Color.GREEN);
        btn_stop.setBackgroundColor(Color.GRAY);
        btn_stop.setEnabled(false);


        if(!runtime_permissions())
            enable_buttons();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeData(id);
                adapter.swapCursor(getAllSesions());
            }
        }).attachToRecyclerView(recyclerView);
    }


    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingsActivity_LoopData.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getAllSesions() {
        return mDb.query(
                StorageContract.StorageEntry.Data_Name,
                null,
                null,
                null,
                null,
                null,
                StorageContract.StorageEntry.COLUMN_TIME
        );
    }

    private long addData(double distance, double maxAltitude, double minAltitude, double velocity, String time){
        ContentValues cv = new ContentValues();
        cv.put(StorageContract.StorageEntry.COLUMN_Datum, getDatum());
        cv.put(StorageContract.StorageEntry.COLUMN_Distance, distance);
        cv.put(StorageContract.StorageEntry.COLUMN_TIME, time);
        cv.put(StorageContract.StorageEntry.COLUMN_MAXALTITUDE, maxAltitude);
        cv.put(StorageContract.StorageEntry.COLUMN_MINALTITUDE, minAltitude);
        cv.put(StorageContract.StorageEntry.COLUMN_VELOCITY, velocity);
        return mDb.insert(StorageContract.StorageEntry.Data_Name, null, cv);
    }

    private boolean removeData(long id) {
        return mDb.delete(StorageContract.StorageEntry.Data_Name, StorageContract.StorageEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onItemClick(View view, int position) {
        Cursor mCursor = adapter.getmCursor();
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String name = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_Datum));
            String distance = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_Distance));
            String time = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_TIME));
            String maxAltitude = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_MAXALTITUDE));
            String minAltitude = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_MINALTITUDE));
            String velocity = mCursor.getString(mCursor.getColumnIndex(StorageContract.StorageEntry.COLUMN_VELOCITY));

            Intent intent = new Intent(this, ChildActivity_LoopData.class);
            intent.putExtra("Intent.EXTRA_TEXT1", name);
            intent.putExtra("Intent.EXTRA_TEXT2", distance);
            intent.putExtra("Intent.EXTRA_TEXT3", time);
            intent.putExtra("Intent.EXTRA_TEXT4", velocity);
            intent.putExtra("Intent.EXTRA_TEXT5", maxAltitude);
            intent.putExtra("Intent.EXTRA_TEXT6", minAltitude);
            startActivity(intent);
        }
    }

    public String getDatum(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return (currentDateTimeString);
    }


    public class FetchAltitude extends AsyncTask<String, Void, ArrayList<Double>> {
        @Override
        protected ArrayList<Double> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            HttpHandler lol = new HttpHandler();
            ArrayList<Double> test = lol.makeServiceCall(params[0]);
            return test;
        }
        @Override
        protected void onPostExecute(ArrayList<Double> respons) {
            DataHandler dataHandler = new DataHandler(locations, respons, date2.getTime() - date1.getTime());
            dataHandler.checkAltitude();

            double distance = dataHandler.getDistance();
            double maxAltitude = dataHandler.getMaxAltitude();
            double minAltitude = dataHandler.getMinAltitude();
            double velocity = dataHandler.getVelocity();
            String time = dataHandler.getTime();

            addData(distance, maxAltitude, minAltitude, velocity, time);

            adapter.swapCursor(getAllSesions());
            locations.clear();

            btn_start.setBackgroundColor(Color.GREEN);
            btn_stop.setBackgroundColor(Color.GRAY);
            btn_start.setEnabled(true);
            btn_stop.setEnabled(false);
        }
    }

    public void stopSession(){
        date2 = Calendar.getInstance().getTime();
        unregisterReceiver(myBroadCastReceiver);
        int teller = 0;
        String http = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
        for (Location location : locations){
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            if (teller == 0){
                http = http + latitude.toString() + "," + longitude.toString();
                teller = teller+1;
            }
            else {
                http = http + "|" + latitude.toString() + "," + longitude.toString();
            }
        }
        http = http + "&key=AIzaSyAOzC0RFR58xkDTlUkZp44ptAVWJei8QlQ";
        new FetchAltitude().execute(http);
    }

    private void registerMyReceiver() {
        try
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    class MyBroadCastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            locations.add((Location) intent.getExtras().get("data"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

    private void startSession(){
        registerMyReceiver();
        date1 = Calendar.getInstance().getTime();
        btn_start.setEnabled(false);
        btn_stop.setEnabled(true);
        btn_start.setBackgroundColor(Color.GRAY);
        btn_stop.setBackgroundColor(Color.RED);
    }

    private void enable_buttons() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSession();
                Intent i =new Intent(getApplicationContext(),LocationService.class);
                i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(i);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LocationService.class);
                stopService(i);
                stopSession();
            }
        });

    }

}
