package com.pustovit.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pustovit Vladimir on 21.06.2019.
 * vovapust1989@gmail.com
 */
enum DownloadStatus {
    IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK
}

class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawDataTag";
    private DownloadStatus downloadStatus;
    private OnDownloadComplete callback;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);
    }


    GetRawData(OnDownloadComplete callback) {
        this.downloadStatus = DownloadStatus.IDLE;
        this.callback = callback;
    }

    void runInSameThread(String s){
        Log.d(TAG, "GetRawData-runInSameThread(): starts");
        onPostExecute(doInBackground(s));
        Log.d(TAG, "GetRawData-runInSameThread(): ends");
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "GetRawData-onPostExecute(): starts ");
        if(callback != null) {
            Log.d(TAG, "GetRawData-onPostExecute(): ends well & callback.onDownloadComplete() starts");
            callback.onDownloadComplete(s, downloadStatus);
        }

    }


    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;


        if (strings == null) {
            downloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }
        try {
            downloadStatus = DownloadStatus.PROCESSING;
            Log.d(TAG, "GetRawData-doInBackground(): Download Status is " + downloadStatus.toString());

            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            Log.d(TAG, "GetRawData-doInBackground(): Response Code is " + responseCode);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder result = new StringBuilder();
            for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                result.append(temp).append("\n");
            }

            downloadStatus = DownloadStatus.OK;
            Log.d(TAG, "GetRawData-doInBackground(): ends well with STATUS "+downloadStatus);
            return result.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "GetRawData-doInBackground(): MalformedURLException: " + e.getMessage());
            downloadStatus = DownloadStatus.NOT_INITIALISED;
        } catch (IOException e) {
            Log.e(TAG, "GetRawData-doInBackground(): IOException: " + e.getMessage());
            downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "GetRawData-doInBackground(): IOException when BufferedReader closed: " + e.getMessage());
            }
        }
        return null;
    }


}
