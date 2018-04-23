package com.example.vanca.loop_application;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity_Loop_Applicatie extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Cursor cursor;
    private TextView loopDataTextView;
    private Button button;
    private MyRecyclerViewAdapter adapter;
    private SQLiteDatabase mDb;
    private ArrayList<Location> locations = new ArrayList<Location>();
    protected LocationManager locationManager;
    protected LocationListener locationListener;

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


        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                startSession();
            }
        });
        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                stopSession();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // COMPLETED (4) Override onMove and simply return false inside
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            // COMPLETED (5) Override onSwiped
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // COMPLETED (8) Inside, get the viewHolder's itemView's tag and store in a long variable id
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                // COMPLETED (9) call removeGuest and pass through that id
                //remove from DB
                removeData(id);
                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                //update the list
                adapter.swapCursor(getAllSesions());
            }

            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(recyclerView);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

    private long addData(double distance){
        ContentValues cv = new ContentValues();
        cv.put(StorageContract.StorageEntry.COLUMN_Datum, getDatum());
        cv.put(StorageContract.StorageEntry.COLUMN_Distance, distance);
        cv.put(StorageContract.StorageEntry.COLUMN_TIME, 1);
        return mDb.insert(StorageContract.StorageEntry.Data_Name, null, cv);
    }

    private boolean removeData(long id) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
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

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String sortBy = prefs.getString("PREF_LIST", "meter");
            if (sortBy == "kilometer"){
                double d = Double.parseDouble(distance);
                double distanceInKm = (d/1000);
                distance = Double.toString(distanceInKm);
            }

            Intent intent = new Intent(this, ChildActivity_LoopData.class);
            intent.putExtra("Intent.EXTRA_TEXT1", name);
            intent.putExtra("Intent.EXTRA_TEXT2", distance);
            intent.putExtra("Intent.EXTRA_TEXT3", time);
            startActivity(intent);
        }
    }

    public String getDatum(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return ("Session   " + currentDateTimeString);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    public class FetchAltitude extends AsyncTask<String, Void, ArrayList<Double>> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
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
            DataHandler dataHandler = new DataHandler(locations, respons);
            double distance = dataHandler.getDistance();
            addData(distance);
            adapter.swapCursor(getAllSesions());
            locations.clear();
        }
    }

    public void startSession(){
        setUp();
        startTracking();
    }

    public void stopSession(){
        stopTracking();
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

    public void setUp() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locations.add(location);
                Log.d("lol", "///////////////////////////////////////////////////////////////////////////////: ");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    public void startTracking() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void stopTracking(){
        locationManager.removeUpdates(locationListener);
        locationListener = null;
    }

}
