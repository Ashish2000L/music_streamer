package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class HttpUtility {

    public static final int METHOD_GET = 0; // METHOD GET
    public static final int METHOD_POST = 1; // METHOD POST

    // Callback interface
    public interface Callback {
        // abstract methods
        public void OnSuccess(String response);
        public void OnError(int status_code, String message);
    }
    // static method
    public static void newRequest(final Context context, final String web_url, final int method, final HashMap< String, String > params, final Callback callback) {

        // thread for handling async task
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = web_url;
                    // write GET params,append with url
                    if (method == METHOD_GET && params != null) {
                        for (Map.Entry<String, String> item : params.entrySet()) {
                            String key = URLEncoder.encode(item.getKey(), "UTF-8");
                            String value = URLEncoder.encode(item.getValue(), "UTF-8");
                            if (!url.contains("?")) {
                                url += "?" + key + "=" + value;
                            } else {
                                url += "&" + key + "=" + value;
                            }
                        }
                    }

                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

                    if(urlConnection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                        Log.d("nice", "run: response code is "+urlConnection.getResponseCode());
                        urlConnection.connect();
                    }else{
                        Log.d("nice", "run: rconection successful "+urlConnection.getResponseCode());
                    }

                    urlConnection.setUseCaches(false);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****"); // handle url encoded form data application/x-www-form-urlencoded
                    urlConnection.setRequestProperty("charset", "utf-8");
                    if (method == METHOD_GET) {
                        urlConnection.setRequestMethod("GET");
                    } else if (method == METHOD_POST) {
                        urlConnection.setDoOutput(true); // write POST params
                        urlConnection.setRequestMethod("POST");
                    }

                    //write POST data
                    if (method == METHOD_POST && params != null) {
                        StringBuilder postData = new StringBuilder();
                        for (Map.Entry<String, String> item : params.entrySet()) {
                            if (postData.length() != 0) postData.append('&');
                            postData.append(URLEncoder.encode(item.getKey(), "UTF-8"));
                            postData.append('=');
                            postData.append(URLEncoder.encode(String.valueOf(item.getValue()), "UTF-8"));
                        }

                        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                        Log.d("nice", "run: content-length "+postDataBytes.length);
                        urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                        urlConnection.getOutputStream().write(postDataBytes);
                        Log.d("nice", "run: writing to server finish ");

                    }
                    // server response code
                    int responseCode = urlConnection.getResponseCode();

                    Log.d("nice", "run: response-code is "+responseCode);

                    if (responseCode == HTTP_OK && callback != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        // callback success
                        callback.OnSuccess(response.toString());
                        reader.close(); // close BufferReader

                    }else if(callback!=null){

                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        StringBuilder error = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            error.append(line);
                        }

                        callback.OnError(responseCode,error.toString());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
