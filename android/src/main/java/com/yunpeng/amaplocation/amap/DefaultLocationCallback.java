package com.yunpeng.amaplocation.amap;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.yunpeng.amaplocation.util.AmapUtils;

/**
 * Created by m2mbob on 16/7/23.
 */
public class DefaultLocationCallback implements AmapLocationCallback {

    private final ReactApplicationContext mReactApplicationContext;
    private final Promise promise;
    private final AMapLocationClient mClient;

    public DefaultLocationCallback(ReactApplicationContext mReactApplicationContext, AMapLocationClient mClient, Promise promise) {
        this.mReactApplicationContext = mReactApplicationContext;
        this.promise = promise;
        this.mClient = mClient;
    }

    @Override
    public void onSuccess(AMapLocation aMapLocation) {
        promise.resolve(AmapUtils.locationToMap(aMapLocation));
        mClient.onDestroy();
    }

    @Override
    public void onFailure(AMapLocation aMapLocation) {
        promise.reject("locating failed: " + aMapLocation.getErrorCode(), new RuntimeException("AMapLocating: locating failed"));
        mClient.onDestroy();
    }


}
