package com.yunpeng.amaplocation.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.yunpeng.amaplocation.sqlite.BackgroundLocation;
import com.yunpeng.amaplocation.sqlite.Config;
import com.yunpeng.amaplocation.sqlite.LocationDao;
import com.yunpeng.amaplocation.util.HttpPostService;

import org.json.JSONException;

import java.util.Collection;

/**
 * Created by m2mbob on 16/7/25.
 */
public class PostLocationService {

    private static final String TAG = "LocationService";
    private LocationDao dao;
    private Config config;

    public PostLocationService(Context context, Config config) {
        this.dao = new LocationDao(context);
        this.config = config;
    }

    public void postLocation() {
        Log.i(TAG, "PostLocation started");

        if(config.getUrl() == null){
            return;
        }
        PostLocationTask task = new PostLocationTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    private class PostLocationTask extends AsyncTask<Void, Integer, Boolean> {

        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "Executing PostLocationTask#doInBackground");
            Collection<BackgroundLocation> locationList = dao.getAllLocations();
            for (BackgroundLocation location: locationList){
                Long locationId = location.getLocationId();
                try {
                    if (HttpPostService.postJSON(config.getUrl(), location.toJSONObject(), config.getHttpHeaders())) {
                        if (locationId != null) {
                            dao.deleteLocation(locationId);
                        }
                    }
                } catch (JSONException e) {
                    Log.w("PostLocationTask", "location to json failed" + location.toString());
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "PostLocationTask#onPostExecture");
        }
    }

}
