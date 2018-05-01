package com.example.vanca.loop_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Van Cappellen Leander
 */

public class LoopDataDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "waitlist.db";
    private static final int DATABASE_VERSION = 1;

    public LoopDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StorageContract.StorageEntry.Data_Name);
        onCreate(sqLiteDatabase);
    }
}
