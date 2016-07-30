package com.yunpeng.amaplocation.amap;

import com.amap.api.location.AMapLocation;
import com.yunpeng.amaplocation.sqlite.BackgroundLocation;
import com.yunpeng.amaplocation.sqlite.LocationDao;

import android.content.Context;
import android.util.Log;

/**
 * Created by m2mbob on 16/7/23.
 */
public class ObserveCallback implements AmapLocationCallback{

    protected static final String TAG = "ObserveCallback";

    private final Context context;

    private LocationDao dao;

    public ObserveCallback(Context context) {
        this.context = context;
        this.dao = new LocationDao(context);
    }

    @Override
    public void onSuccess(AMapLocation aMapLocation) {
        //TODO persist the aMapLocation
        if (dao.persistLocation(new BackgroundLocation(aMapLocation)) > -1) {
            Log.d(TAG, "Persisted Location: " + aMapLocation.toString());
        } else {
            Log.w(TAG, "Failed to persist location");
        }
    }

    @Override
    public void onFailure(AMapLocation aMapLocation) {
        //
    }
}
