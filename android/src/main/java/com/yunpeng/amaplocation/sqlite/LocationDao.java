package com.yunpeng.amaplocation.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.amap.api.location.AMapLocation;
import com.yunpeng.amaplocation.sqlite.LocationContract.LocationEntry;

/**
 * Created by m2mbob on 16/7/23.
 */
public class LocationDao {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
    private static final String TAG = "LocationDao";
    private Context context;

    public LocationDao(Context context) {
        this.context = context;
    }

    public Collection<BackgroundLocation> getAllLocations() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_TIME,
                LocationEntry.COLUMN_NAME_ACCURACY,
                LocationEntry.COLUMN_NAME_LATITUDE,
                LocationEntry.COLUMN_NAME_LONGITUDE,
                LocationEntry.COLUMN_NAME_ADDRESS,
                LocationEntry.COLUMN_NAME_COUNTRY,
                LocationEntry.COLUMN_NAME_PROVINCE,
                LocationEntry.COLUMN_NAME_CITY,
                LocationEntry.COLUMN_NAME_DISTRICT,
                LocationEntry.COLUMN_NAME_STREET,
                LocationEntry.COLUMN_NAME_CITY_CODE,
                LocationEntry.COLUMN_NAME_AD_CODE,
                LocationEntry.COLUMN_NAME_POI_NAME,
                LocationEntry.COLUMN_NAME_AOI_NAME,
                LocationEntry.COLUMN_NAME_PROVIDER
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy =
                LocationEntry.COLUMN_NAME_TIME + " ASC";

        Collection<BackgroundLocation> all = new ArrayList<BackgroundLocation>();
        try {
            db = new LocationOpenHelper(context).getReadableDatabase();
            cursor = db.query(
                    LocationEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (cursor.moveToNext()) {
                all.add(hydrate(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return all;
    }

    public Long persistLocation(BackgroundLocation location) {
        SQLiteDatabase db = new LocationOpenHelper(context).getWritableDatabase();
        db.beginTransaction();
        ContentValues values = getContentValues(location);
        long rowId = db.insert(LocationEntry.TABLE_NAME, "nullColumnHack", values);
        Log.d(TAG, "After insert, rowId = " + rowId);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return rowId;
    }

    public void deleteLocation(Long locationId) {
        String whereClause = LocationEntry._ID + " = ?";
        String[] whereArgs = { String.valueOf(locationId) };
        SQLiteDatabase db = new LocationOpenHelper(context).getWritableDatabase();
        db.beginTransaction();
        db.delete(LocationEntry.TABLE_NAME, whereClause, whereArgs);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteAllLocations() {
        SQLiteDatabase db = new LocationOpenHelper(context).getWritableDatabase();
        db.beginTransaction();
        db.delete(LocationEntry.TABLE_NAME, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private BackgroundLocation hydrate(Cursor c) {
        BackgroundLocation l = new BackgroundLocation(new AMapLocation(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_PROVIDER))));
        l.setLocationId(c.getLong(c.getColumnIndex(LocationEntry._ID)));
        l.setTime(c.getLong(c.getColumnIndex(LocationEntry.COLUMN_NAME_TIME)));
        l.setAccuracy(c.getFloat(c.getColumnIndex(LocationEntry.COLUMN_NAME_ACCURACY)));
        l.setLatitude(c.getDouble(c.getColumnIndex(LocationEntry.COLUMN_NAME_LATITUDE)));
        l.setLongitude(c.getDouble(c.getColumnIndex(LocationEntry.COLUMN_NAME_LONGITUDE)));
        l.setAddress(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_ADDRESS)));
        l.setCountry(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_COUNTRY)));
        l.setProvince(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_PROVINCE)));
        l.setCity(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_CITY)));
        l.setDistrict(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_DISTRICT)));
        l.setStreet(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_STREET)));
        l.setCity_code(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_CITY_CODE)));
        l.setAd_code(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_AD_CODE)));
        l.setPoi_name(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_POI_NAME)));
        l.setAoi_name(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_AOI_NAME)));
        l.setProvider(c.getString(c.getColumnIndex(LocationEntry.COLUMN_NAME_PROVIDER)));

        return l;
    }

    private ContentValues getContentValues(BackgroundLocation location) {
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_NAME_TIME, location.getTime());
        values.put(LocationEntry.COLUMN_NAME_ACCURACY, location.getAccuracy());
        values.put(LocationEntry.COLUMN_NAME_LATITUDE, location.getLatitude());
        values.put(LocationEntry.COLUMN_NAME_LONGITUDE, location.getLongitude());
        values.put(LocationEntry.COLUMN_NAME_ADDRESS, location.getAddress());
        values.put(LocationEntry.COLUMN_NAME_COUNTRY, location.getCountry());
        values.put(LocationEntry.COLUMN_NAME_PROVINCE, location.getProvince());
        values.put(LocationEntry.COLUMN_NAME_CITY, location.getCity());
        values.put(LocationEntry.COLUMN_NAME_DISTRICT, location.getDistrict());
        values.put(LocationEntry.COLUMN_NAME_STREET, location.getStreet());
        values.put(LocationEntry.COLUMN_NAME_CITY_CODE, location.getCity_code());
        values.put(LocationEntry.COLUMN_NAME_AD_CODE, location.getAd_code());
        values.put(LocationEntry.COLUMN_NAME_POI_NAME, location.getPoi_name());
        values.put(LocationEntry.COLUMN_NAME_AOI_NAME, location.getAoi_name());
        values.put(LocationEntry.COLUMN_NAME_PROVIDER, location.getProvider());

        return values;
    }

}
