package com.example.visualpost_it.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {

    private static final String TAG = "DownloadUrl";

    public String readUrl(String myUrl) throws IOException {
        String data = "";

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            Log.d(TAG, "readUrl: " + url.toString());

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            Log.d(TAG, "readUrl: Malformed URL exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "readUrl: IOException");
            e.printStackTrace();
        }

        if (inputStream != null) {
            inputStream.close();
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }

        return data;
    }


}
