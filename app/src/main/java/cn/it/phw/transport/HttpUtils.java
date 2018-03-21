package cn.it.phw.transport;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *
 * Created by phw on 18-3-20.
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    public static final String SERVER = "http://192.168.40.116:8080/transportservice/type/jason/action/";

    public String urlPath = null;
    public Handler handler = null;

    public HttpUtils(final String urlPath) {
        this.urlPath = urlPath;
    }

    public HttpUtils(final String urlPath, Handler handler) {
        this.urlPath = urlPath;
        this.handler = handler;
    }

    public String get() {
        if (urlPath == null) {
            return null;
        }
        String result = null;
        FutureTask<String> task = new FutureTask<>(() -> {

            BufferedReader br = null;
            InputStreamReader isr = null;
            URLConnection conn = null;

            StringBuilder sb = new StringBuilder();
            try {
                URL url1 = new URL(urlPath);
                conn = url1.openConnection();
                isr = new InputStreamReader(conn.getInputStream());
                br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Log.d(TAG, sb.toString());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return sb.toString();
        });

        new Thread(task).start();
        try {
            result = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String post(Map<String, Object> params) {

        String jsonParamStr = parseDataMap(params);
        Log.d(TAG, jsonParamStr);
        FutureTask<String> task = new FutureTask<>(() -> {
            BufferedReader br = null;
            BufferedWriter bw = null;
            HttpURLConnection conn = null;

            StringBuilder sb = new StringBuilder();
            try {
                URL url1 = new URL(urlPath);
                conn = (HttpURLConnection) url1.openConnection();

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                bw.write(jsonParamStr);
                bw.flush();
                bw.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e(TAG, sb.toString());
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return sb.toString();

        });

        new Thread(task).start();
        String result = null;
        try {
            result = task.get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public void getByHandler(Handler handler) {

    }

    public void postByHandler(Map<String, Object> dataMap) {
        String jsonParam = parseDataMap(dataMap);
        StringBuilder sb = new StringBuilder();
        BufferedWriter bw = null;
        BufferedReader br = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(jsonParam);
            bw.flush();
            bw.close();

            if (conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Log.d(TAG, sb.toString());
            }
            Message message = handler.obtainMessage(0);
            message.obj = sb.toString();
            handler.sendMessage(message);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Parsed data map to json string
     * @param dataMap map
     * @return json string
     */
    private String parseDataMap(Map<String, Object> dataMap) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry: dataMap.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject.toString();
    }

}
