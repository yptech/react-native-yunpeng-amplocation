package com.yunpeng.amaplocation.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by m2mbob on 16/7/23.
 * 将记录的json数据提交到指定的url
 */
public class HttpPostService {

    public static boolean postJSON(String url, JSONObject json, Map headers)	{
        try {
            Log.d("AMapLocating", "Posting json: " + json.toString() + " to url: " + url + " headers: " + headers.toString());

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // conn.setConnectTimeout(5000);
            conn.setChunkedStreamingMode(0);
            conn.setDoOutput(true);
            // conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                conn.setRequestProperty(pair.getKey(), pair.getValue());
            }

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(json.toString());
            os.flush();
            os.close();

            Log.d("AMapLocating", "Posting json response code: " + conn.getResponseCode());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }

            return false;

        } catch (Throwable e) {
            Log.w("AMapLocating", "Exception posting json: " + e);
            e.printStackTrace();
            return false;
        }
    }

}
