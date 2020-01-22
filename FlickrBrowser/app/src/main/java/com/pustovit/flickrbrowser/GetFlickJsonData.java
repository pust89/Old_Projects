package com.pustovit.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pustovit Vladimir on 23.06.2019.
 * vovapust1989@gmail.com
 */

public class GetFlickJsonData extends AsyncTask<String, Void, List<Photo>>implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickJsonDataTag";
    private List<Photo> photoList;
    private String baseUrl;
    private String language;
    private boolean matchAll;
    private final OnDataAvailable callBack;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> photoList, DownloadStatus status);
    }

    GetFlickJsonData(OnDataAvailable callBack, String baseUrl, String language, boolean matchAll) {
        this.baseUrl = baseUrl;
        this.language = language;
        this.matchAll = matchAll;
        this.callBack = callBack;
    }



    @Override
    protected void onPostExecute(List<Photo> photoList) {
        Log.d(TAG, "GetFlickJsonData-onPostExecute(): starts");
        if(callBack != null){
            callBack.onDataAvailable(photoList, DownloadStatus.OK);
        }
        Log.d(TAG, "GetFlickJsonData-onPostExecute(): ends");
    }

    @Override
    protected List<Photo> doInBackground(String... strings) {
        Log.d(TAG, "GetFlickJsonData-doInBackground(): starts");
        String destinationUri = createUri(strings[0],language, matchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "GetFlickJsonData-doInBackground(): ends");
        return photoList;
    }

    String createUri(String searchCriteria, String language, boolean matchAll){
        Log.d(TAG, "GetFlickJsonData-createUri(): starts");
        String result = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("lang", language)
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback","1")
                .build()
                .toString();
        Log.d(TAG, "GetFlickJsonData-createUri() returned: " + result);
        return result;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "GetFlickJsonData-onDownloadComplete(): starts with STATUS = "+status);

        if (status == DownloadStatus.OK){

            photoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray jsonArray = jsonData.getJSONArray("items");

                JSONObject tempItem;
                String tempTitle;
                String tempAuthor;
                String tempAuthorId;
                String tempTags;
                String tempImage;

                for(int i = 0; i < jsonArray.length(); i++){
                    tempItem = (JSONObject) jsonArray.get(i);

                    tempTitle = tempItem.getString("title");
                    tempAuthor = tempItem.getString("author");
                    tempAuthorId = tempItem.getString("author_id");
                    tempTags = tempItem.getString("tags");

                    tempItem = tempItem.getJSONObject("media");
                    tempImage = tempItem.getString("m");

                    photoList.add(new Photo(tempTitle,tempAuthor,tempAuthorId,tempImage, tempTags));
                }


            } catch (JSONException e) {
                Log.e(TAG, "GetFlickJsonData-onDownloadComplete(): Error processing Json data "+e.getMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }

        }
    }
}
