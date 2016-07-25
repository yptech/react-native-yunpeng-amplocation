package com.yunpeng.amaplocation.amap;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by m2mbob on 16/7/23.
 */
public class YPAmapLocationListener implements AMapLocationListener{

    private AmapLocationCallback mCallback;

    public YPAmapLocationListener(AmapLocationCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            Log.e("AMapLocating", "received amaplocation is null");
            return;
        }

        if (aMapLocation.getErrorCode() == 0) {
            Log.d("AMapLocating", "location info: " + aMapLocation.getLatitude() + " - " + aMapLocation.getLongitude() + " / " + aMapLocation.getLocationDetail());
            if (mCallback != null) {
                mCallback.onSuccess(aMapLocation);
            }
        } else {
            Log.e("AMapLocating", "locating error code: " + aMapLocation.getErrorCode() + " - error info: " + aMapLocation.getErrorInfo());
            if (mCallback != null) {
                mCallback.onFailure(aMapLocation);
            }
        }
    }

}
