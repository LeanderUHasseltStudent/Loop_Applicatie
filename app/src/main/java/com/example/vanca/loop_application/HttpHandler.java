package com.example.vanca.loop_application;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;

public class HttpHandler{

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public Double makeServiceCall(String reqUrl) {
        Double response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());

            BufferedReader bR = new BufferedReader(  new InputStreamReader(in));
            String line = "";
            StringBuilder responseStrBuilder = new StringBuilder();
            while((line =  bR.readLine()) != null){
                responseStrBuilder.append(line);
            }
            in.close();
            JSONObject jsonResult= new JSONObject(responseStrBuilder.toString());

            JSONArray data = jsonResult.getJSONArray("results");
            if(data != null) {
                String[] birthdays = new String[data.length()];
                for(int i = 0 ; i < data.length() ; i++) {
                    birthdays[i] = data.getJSONObject(i).getString("elevation");
                }
                double value = Double.parseDouble(birthdays[0]);
                response = value;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JsonException: " + e.getMessage());
        }
        return response;
    }
}
