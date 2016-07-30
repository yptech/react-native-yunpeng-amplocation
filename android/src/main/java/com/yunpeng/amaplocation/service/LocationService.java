package com.yunpeng.amaplocation.service;

import java.util.Random;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.yunpeng.amaplocation.ContextHolder;
import com.yunpeng.amaplocation.amap.AmapLocationCallback;
import com.yunpeng.amaplocation.amap.YPAmapLocationListener;
import com.yunpeng.amaplocation.sqlite.Config;
import com.yunpeng.amaplocation.util.AmapUtils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by m2mbob on 16/7/23.
 */
public class LocationService extends Service implements AmapLocationCallback {

	private static final String TAG = "LocationService";
	private AMapLocationClient oClient;
//	private LocationDao dao;
	private Config config;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		dao = new LocationDao(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);

		if (intent.hasExtra("config")) {
			config = (Config) intent.getParcelableExtra("config");
		} else {
			config = new Config();
		}

		AMapLocationClientOption option = AmapUtils.getOptions(config, false);
		oClient = new AMapLocationClient(this);
		oClient.setLocationOption(option);
		YPAmapLocationListener listener = new YPAmapLocationListener(this);
		oClient.setLocationListener(listener);
		oClient.startLocation();

		if (config.getStartForeground()) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
			builder.setContentTitle(config.getNotificationTitle());
			builder.setContentText(config.getNotificationText());

			Notification notification = builder.build();
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE
					| Notification.FLAG_NO_CLEAR;
			startForeground(startId, notification);
		}

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		oClient.onDestroy();
		super.onDestroy();
	}

	/**
	 * Adds an onclick handler to the notification
	 */
	protected NotificationCompat.Builder setClickEvent(NotificationCompat.Builder builder) {
		int requestCode = new Random().nextInt();
		Context context = getApplicationContext();
		String packageName = context.getPackageName();
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, launchIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		return builder.setContentIntent(contentIntent);
	}

	private Integer parseNotificationIconColor(String color) {
		int iconColor = 0;
		if (color != null) {
			try {
				iconColor = Color.parseColor(color);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "couldn't parse color from android options");
			}
		}
		return iconColor;
	}

	@Override
	public void onSuccess(AMapLocation aMapLocation) {
		ReactApplicationContext context = ContextHolder.getReactApplicationContext();
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("locationDidChange",
				AmapUtils.locationToMap(aMapLocation));
	}

	@Override
	public void onFailure(AMapLocation aMapLocation) {
		ReactApplicationContext context = ContextHolder.getReactApplicationContext();
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("locationError",
				AmapUtils.locationToMap(aMapLocation));
	}

}
