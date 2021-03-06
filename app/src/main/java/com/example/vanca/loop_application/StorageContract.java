package com.example.vanca.loop_application;

import android.provider.BaseColumns;

/**
 * Created by Van Cappellen Leander
 */

public class StorageContract {

    public static final class StorageEntry implements BaseColumns {
        public static final String Data_Name = "loopData";
        public static final String COLUMN_Datum = "datum";
        public static final String COLUMN_Distance = "partySize";
        public static final String COLUMN_TIME= "timestamp";
        public static final String COLUMN_VELOCITY= "velocity";
        public static final String COLUMN_MAXALTITUDE= "maxAltitude";
        public static final String COLUMN_MINALTITUDE= "minAltitude";
    }

}
