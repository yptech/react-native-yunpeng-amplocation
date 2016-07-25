package com.yunpeng.amaplocation.amap;

import com.amap.api.location.AMapLocation;

/**
 * Created by m2mbob on 16/7/23.
 */
public interface AmapLocationCallback {
    void onSuccess(AMapLocation aMapLocation);
    void onFailure(AMapLocation aMapLocation);
}