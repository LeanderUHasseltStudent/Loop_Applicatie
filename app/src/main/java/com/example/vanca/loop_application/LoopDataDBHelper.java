package com.example.vanca.loop_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LoopDataDBHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "waitlist.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public LoopDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + StorageContract.StorageEntry.Data_Name + " (" +
                StorageContract.StorageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StorageContract.StorageEntry.COLUMN_Datum + " TEXT NOT NULL, " +
                StorageContract.StorageEntry.COLUMN_Distance + " REAL, " +
                StorageContract.StorageEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                StorageContract.StorageEntry.COLUMN_VELOCITY+ " REAL, " +
                StorageContract.StorageEntry.COLUMN_MAXALTITUDE + " REAL, " +
                StorageContract.StorageEntry.COLUMN_MINALTITUDE + " REAL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StorageContract.StorageEntry.Data_Name);
        onCreate(sqLiteDatabase);
    }
}
