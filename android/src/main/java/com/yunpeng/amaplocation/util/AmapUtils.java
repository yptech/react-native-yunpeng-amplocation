package com.yunpeng.amaplocation.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.yunpeng.amaplocation.sqlite.Config;

/**
 * Created by m2mbob on 16/7/23.
 */
public class AmapUtils {

    public static WritableMap locationToMap(AMapLocation location) {
        if (location == null) {
            return null;
        }

        WritableMap map = Arguments.createMap();
        WritableMap coords = Arguments.createMap();
        coords.putDouble("latitude", location.getLatitude());
        coords.putDouble("longitude", location.getLongitude());
        coords.putString("address", location.getAddress());
        coords.putDouble("accuracy", location.getAccuracy());
        coords.putDouble("heading", location.getBearing());
        map.putMap("coords", coords);
        map.putDouble("timestamp", location.getTime());

        return map;

    }

    /**
     * 获取定位参数
     * @param options
     * @param once
     * @return
     */
    public static AMapLocationClientOption getOptions(ReadableMap options, final boolean once) {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);   //高精度定位,采取GPS和网络定位结果中比较高的
        // 默认定位事件间隔30000ms,如果传入了别的参数则选择新的间隔
        if(options.hasKey("interval")){
            option.setInterval(Long.parseLong(options.getString("interval")));
        }
        option.setOnceLocation(once);
        return option;
    }

    /**
     * 获取定位参数
     * @param options
     * @param once
     * @return
     */
    public static AMapLocationClientOption getOptions(Config options, final boolean once) {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);   //高精度定位,采取GPS和网络定位结果中比较高的
        if (options.getInterval() != null){
            option.setInterval(options.getInterval());
        }
        option.setOnceLocation(once);
        return option;
    }

}
