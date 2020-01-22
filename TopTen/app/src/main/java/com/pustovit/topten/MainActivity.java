package com.pustovit.topten;

/**
 * Created by Pustovit Vladimir on 28.05.2019.
 * vovapust1989@gmail.com
 */

import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements DownloadData.CallBack{
    private static final String TAG = "DebugMainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;

    private String currentUrl;
    private int currentLimit;
    private String xmlStringForSave;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_URL", currentUrl);
        outState.putInt("CURRENT_LIMIT", currentLimit);
        outState.putString("XML_DATA", xmlStringForSave);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = findViewById(R.id.listView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentUrl != null){
            downloadUrl(currentUrl, currentLimit);
        }else{
            downloadUrl(feedUrl, feedLimit);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            Log.d(TAG, "onRestoreInstanceState: is not null!");
            currentUrl = savedInstanceState.getString("CURRENT_URL");
            currentLimit = savedInstanceState.getInt("CURRENT_LIMIT");

            feedUrl = currentUrl;
            feedLimit = currentLimit;

            xmlStringForSave = savedInstanceState.getString("XML_DATA");
            
            Parser parser = new Parser();
            parser.parse(xmlStringForSave);
            Log.d(TAG, "SIZE() = "+ parser.getApplications().size());

            FeedAdapter<FeedEntry> feedAdapter = new FeedAdapter<>(this,R.layout.list_record, parser.getApplications());
            listApps.setAdapter(feedAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(currentLimit != 0) {
            if (currentLimit == 10) {
                menu.findItem(R.id.top10).setChecked(true);
            } else {
                menu.findItem(R.id.top25).setChecked(true);
            }
        } else{
            if (feedLimit == 10){
                menu.findItem(R.id.top10).setChecked(true);
            } else {
                menu.findItem(R.id.top25).setChecked(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.top10:
                if(!item.isChecked()) {
                    feedLimit = 10;
                    item.setChecked(true);
                }
                break;
            case R.id.top25:
                if(!item.isChecked()) {
                    feedLimit = 25;
                    item.setChecked(true);
                }
                break;

            case R.id.menuRefresh:
                DownloadData downloadData = new DownloadData(this);
                downloadData.execute(String.format(currentUrl, currentLimit));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadUrl(feedUrl, feedLimit);

        return true;
    }



    private void downloadUrl(String feedUrl, int feedLimit) {

        if(currentUrl != null && currentLimit != 0){

            if(!currentUrl.equalsIgnoreCase(feedUrl) || currentLimit != feedLimit){
                
                Log.d(TAG, "downloadUrl: _2_ starts downloading");
                DownloadData downloadData = new DownloadData(this);
                
                if(!currentUrl.equalsIgnoreCase(feedUrl) && currentLimit != feedLimit) {
                    downloadData.execute(String.format(currentUrl, currentLimit));
                    Log.d(TAG, "downloadUrl: 1");
                } 
                if(!currentUrl.equalsIgnoreCase(feedUrl) && currentLimit == feedLimit) {
                    downloadData.execute(String.format(feedUrl, currentLimit));
                    Log.d(TAG, "downloadUrl: 2");
                }
                if (currentUrl.equalsIgnoreCase(feedUrl) && currentLimit != feedLimit) {
                    downloadData.execute(String.format(currentUrl, feedLimit));
                    Log.d(TAG, "downloadUrl: 3");
                }


                Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++"+"\n");
            } else{
                Log.d(TAG, "downloadUrl: Nothing to do, same URL");
            }
        }else{
            Log.d(TAG, "downloadUrl: _1_  starts first start downloading");

            DownloadData downloadData = new DownloadData(this);
            downloadData.execute(String.format(feedUrl, feedLimit));
            Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++"+"\n");
        }
        currentUrl = feedUrl;
        currentLimit = feedLimit;
    }

    @Override
    public void getStringResultForSave(String s) {
        xmlStringForSave = s;
    }
}
