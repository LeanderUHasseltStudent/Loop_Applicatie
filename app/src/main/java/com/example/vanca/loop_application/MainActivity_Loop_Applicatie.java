package com.example.vanca.loop_application;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static android.support.v4.content.ContextCompat.startActivity;

public class MainActivity_Loop_Applicatie extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private Cursor cursor;
    private TextView loopDataTextView;
    private Button button;
    private MyRecyclerViewAdapter adapter;
    private LocationManagment locationManagment;
    private SQLiteDatabase mDb;
    private ArrayList<String> locaties = new ArrayList<>();

    public MainActivity_Loop_Applicatie() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__loop__applicatie);

        LoopDataDBHelper dbHelper = new LoopDataDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        cursor = getAllSesions();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, cursor);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        Button button = (Button) findViewById(R.id.myButton);
        button.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                addData();
                adapter.swapCursor(getAllSesions());
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

    private long addData(){
        ContentValues cv = new ContentValues();
        cv.put(StorageContract.StorageEntry.COLUMN_Datum, getDatum());
        cv.put(StorageContract.StorageEntry.COLUMN_Distance, 1);
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
        }

        new FetchAltitude().execute("https://maps.googleapis.com/maps/api/elevation/json?locations=36.455556,-116.866667&key=AIzaSyAOzC0RFR58xkDTlUkZp44ptAVWJei8QlQ");



        Intent intent = new Intent(this, ChildActivity_LoopData.class);
        startActivity(intent);
    }

    public String getDatum(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return ("Session   " + currentDateTimeString);
    }



    public class FetchAltitude extends AsyncTask<String, Void, Double> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected Double doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            HttpHandler lol = new HttpHandler();
            Double test = lol.makeServiceCall(params[0]);
            return test;
        }
        @Override
        protected void onPostExecute(Double respons) {
            Log.d("lol", respons + "     //////////////////////////////////////////////////////////////////////////////////////////");
        }
    }
}
