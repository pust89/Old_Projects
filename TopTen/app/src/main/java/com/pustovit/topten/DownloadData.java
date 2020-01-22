package com.pustovit.topten;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pustovit Vladimir on 29.05.2019.
 * vovapust1989@gmail.com
 */

public class DownloadData extends AsyncTask<String, Void, String> {
    private static final String TAG = "DownloadData";
    private String resultForSave;

    @SuppressLint("StaticFieldLeak")
    private MainActivity  mainActivity;

    interface CallBack {
        void getStringResultForSave(String s);
    }

    DownloadData(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Parser parser = new Parser();
        parser.parse(s);
        mainActivity.getStringResultForSave(s);

        ListView listView = mainActivity.findViewById(R.id.listView);
        FeedAdapter<FeedEntry> feedAdapter = new FeedAdapter<>(mainActivity, R.layout.list_record, parser.getApplications());
        listView.setAdapter(feedAdapter);
    }



    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String rssFeed = downloadXML(strings[0]);

        if (rssFeed == null) {
            Log.e(TAG, "doInBackground: error downloading");
        }
        return rssFeed;
    }


    private String downloadXML(String urlPath) {

        StringBuilder xmlResult = new StringBuilder();

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadXML: response code was " + response);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                xmlResult.append(temp).append("\n");
            }
            reader.close();
            return xmlResult.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: MalformedURLException Invalid URL " + e.getMessage());

        } catch (
                IOException e) {
            Log.e(TAG, "downloadXML: IOException reading data " + e.getMessage());
        }

        return null;
    }
}
