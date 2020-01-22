package com.pustovit.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickJsonData.OnDataAvailable,
        RecyclerItemClickListener.OnRecyclerClickListener
{
    private static final String TAG = "MainActivityTag";
    private static String BASE_URL = "https://www.flickr.com/services/feeds/photos_public.gne";
    private RecyclerView recyclerView;
    private FlickrRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar(false);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView, this));

        adapter = new FlickrRecyclerViewAdapter(new ArrayList<Photo>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("FLICKR_Sh_Pref", MODE_PRIVATE);

        String queryResult = sharedPreferences.getString(FLICKR_QUERY,"");

            if(queryResult.length()>0) {
                GetFlickJsonData getFlickJsonData = new GetFlickJsonData(this, BASE_URL, "en-us", true);
                getFlickJsonData.execute(queryResult);
            }
    }


    @Override
    public void onDataAvailable(List<Photo> photoList, DownloadStatus status) {
        Log.d(TAG, "MainActivity-onDataAvailable(): starts with STATUS "+ status);
        if (status == DownloadStatus.OK) {

            adapter.loadNewData(photoList);

        }   else{
            Log.e(TAG, "MainActivity-onDataAvailable(): failed with status " + status);
        }
        Log.d(TAG, "MainActivity-onDataAvailable(): ends");

    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this,"One click, position is "+ position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Photo photo = adapter.getPhoto(position);
        if(photo!= null) {
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra(PHOTO_TRANSFER, photo);
            startActivity(intent);
        }
    }
}
