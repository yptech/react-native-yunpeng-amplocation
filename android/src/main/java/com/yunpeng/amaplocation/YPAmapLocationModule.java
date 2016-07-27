package com.yunpeng.amaplocation;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.common.SystemClock;
import com.yunpeng.amaplocation.amap.DefaultLocationCallback;
import com.yunpeng.amaplocation.amap.YPAmapLocationListener;
import com.yunpeng.amaplocation.service.LocationService;
import com.yunpeng.amaplocation.service.PostLocationService;
import com.yunpeng.amaplocation.sqlite.Config;
import com.yunpeng.amaplocation.util.AmapUtils;

import java.util.HashMap;

/**
 * Created by m2mbob on 16/7/23.
 */
public class YPAmapLocationModule extends ReactContextBaseJavaModule {

    protected static final String TAG = "YPAmapLocation";

    private AMapLocationClient mClient;
    private Config mConfig;
    /** Flag indicating whether we have called bind on the service. */
    private Boolean mIsServiceRunning = false;

    public YPAmapLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "YPAmapLocation";
    }

    /**
     * 配置插件
     * @param options
     * @param promise
     */
    @ReactMethod
    public void configure(ReadableMap options, Promise promise) {
        Log.d(TAG, "configure called");
        Config config = new Config();
        if (options.hasKey("notificationTitle")) config.setNotificationTitle(options.getString("notificationTitle"));
        if (options.hasKey("notificationText")) config.setNotificationText(options.getString("notificationText"));
        if (options.hasKey("notificationIconColor")) config.setNotificationIconColor(options.getString("notificationIconColor"));
        if (options.hasKey("stopOnTerminate")) config.setStopOnTerminate(options.getBoolean("stopOnTerminate"));
        if (options.hasKey("startOnBoot")) config.setStartOnBoot(options.getBoolean("startOnBoot"));
        if (options.hasKey("startForeground")) config.setStartForeground(options.getBoolean("startForeground"));
        if (options.hasKey("interval")) config.setInterval(options.getInt("interval"));
        if (options.hasKey("stopOnStillActivity")) config.setStopOnStillActivity(options.getBoolean("stopOnStillActivity"));
        if (options.hasKey("url")) config.setUrl(options.getString("url"));
        if (options.hasKey("httpHeaders")) {
            HashMap httpHeaders = new HashMap<String, String>();
            ReadableMap rm = options.getMap("httpHeaders");
            ReadableMapKeySetIterator it = rm.keySetIterator();

            while (it.hasNextKey()) {
                String key = it.nextKey();
                httpHeaders.put(key, rm.getString(key));
            }

            config.setHttpHeaders(httpHeaders);
        }
        this.mConfig = config;

        Log.d(TAG, "bg service configured: " + config.toString());
        promise.resolve(true);
    }

    /**
     * 获取定位
     * @param options
     * @param promise
     */
    @ReactMethod
    public void getCurrentPosition(ReadableMap options, Promise promise){
        YPAmapLocationListener listener = null;
        AMapLocationClientOption option = null;
        if(mClient != null){
            // 如果最近依次定位在一秒内则直接返回
            AMapLocation lastLocation = mClient.getLastKnownLocation();
            if (lastLocation != null) {
                long locationTime = lastLocation.getTime();
                if (locationTime > 0 && (SystemClock.currentTimeMillis() - locationTime < 1000)) {

                    promise.resolve(AmapUtils.locationToMap(lastLocation));
                    return;
                }
            }
        }
        // 开始一次新的单次定位
        mClient = new AMapLocationClient(getReactApplicationContext());
        option = AmapUtils.getOptions(options, true);
        listener = new YPAmapLocationListener(new DefaultLocationCallback(getReactApplicationContext(), mClient, promise));
        mClient.setLocationOption(option);
        mClient.setLocationListener(listener);
        mClient.startLocation();
    }

    /**
     * 开启后台监听
     * @param promise
     */
    @ReactMethod
    public void startObserving(Promise promise) {
        if (hasPermissions()) {
            startBackgroundService();
            promise.resolve(true);
        } else {
            //TODO: requestPermissions
        }
    }

    public boolean hasPermissions() {
        //TODO: implement
        return true;
    }

    /**
     * 停止监听,并将sqlite中的数据上传,并清空
     */
    @ReactMethod
    public void stopObserving(Promise promise) {
        stopBackgroundService();
        PostLocationService postLocationService = new PostLocationService(getReactApplicationContext(), mConfig);
        postLocationService.postLocation();
        promise.resolve(true);
    }

    /**
     * 开启监听服务
     */
    protected void startBackgroundService() {
        if (mIsServiceRunning) { return; }

        final Activity currentActivity = this.getCurrentActivity();
        Intent locationServiceIntent = new Intent(currentActivity, LocationService.class);
        locationServiceIntent.putExtra("config", mConfig);
        locationServiceIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        // start service to keep service running even if no clients are bound to it
        currentActivity.startService(locationServiceIntent);
        mIsServiceRunning = true;
    }

    /**
     * 关闭监听服务
     */
    protected void stopBackgroundService() {
        if (!mIsServiceRunning) { return; }

        Log.d(TAG, "Stopping bg service");
        final Activity currentActivity = this.getCurrentActivity();
        currentActivity.stopService(new Intent(currentActivity, LocationService.class));
        mIsServiceRunning = false;
    }

}
