package com.yunpeng.amaplocation.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yunpeng.amaplocation.sqlite.LocationContract.LocationEntry;

/**
 * Created by m2mbob on 16/7/23.
 */
public class LocationOpenHelper extends SQLiteOpenHelper {
    private static final String SQLITE_DATABASE_NAME = "react_native_amapLocation.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_LOCATION_TABLE =
            "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                    LocationEntry._ID + " INTEGER PRIMARY KEY," +
                    LocationEntry.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_LONGITUDE + REAL_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_ACCURACY + REAL_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_COUNTRY + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_PROVINCE + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_CITY + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_DISTRICT + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_STREET + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_CITY_CODE + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_AD_CODE + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_POI_NAME + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_AOI_NAME + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_PROVIDER + TEXT_TYPE + COMMA_SEP +
                    LocationEntry.COLUMN_NAME_TIME + INTEGER_TYPE +
                    " )";

    private static final String SQL_DELETE_LOCATION_TABLE =
            "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;

    public LocationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LocationOpenHelper(Context context) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        Log.d(this.getClass().getName(), SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_LOCATION_TABLE);
        Log.d(this.getClass().getName(), SQL_DELETE_LOCATION_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
