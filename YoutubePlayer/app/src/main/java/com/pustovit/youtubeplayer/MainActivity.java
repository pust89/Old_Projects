package com.pustovit.youtubeplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlaySingleVideo = findViewById(R.id.btnPlaySingle);
        Button btnStandaloneMenu = findViewById(R.id.btnPlayStandalone);

        btnPlaySingleVideo.setOnClickListener(this);
        btnStandaloneMenu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.btnPlaySingle:
                intent = new Intent(this, YoutubeActivity.class);
                break;
            case R.id.btnPlayStandalone:
                intent = new Intent(this, StandaloneActivity.class);
                break;

                default:
        }

        if( intent != null){
            startActivity(intent);
        }
    }
}
