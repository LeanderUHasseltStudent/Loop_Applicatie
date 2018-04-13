package com.example.vanca.loop_application;

import android.provider.BaseColumns;


public class StorageContract {

    public static final class StorageEntry implements BaseColumns {
        public static final String Data_Name = "loopData";
        public static final String COLUMN_Datum = "datum";
        public static final String COLUMN_Distance = "partySize";
        public static final String COLUMN_TIME= "timestamp";
    }

}
