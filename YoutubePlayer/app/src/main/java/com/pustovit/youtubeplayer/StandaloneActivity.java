package com.pustovit.youtubeplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

/**
 * Created by Pustovit Vladimir on 18.06.2019.
 * vovapust1989@gmail.com
 */

public class StandaloneActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);
        Button btbPlayVideo = findViewById(R.id.btnPlayVideo);
        Button btnPlayPlaylist = findViewById(R.id.btnPlayPlaylist);
        btnPlayPlaylist.setOnClickListener(this);
        btbPlayVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnPlayVideo:
                intent = YouTubeStandalonePlayer.createVideoIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_VIEDO_ID, 0, true, false);
                break;
            case R.id.btnPlayPlaylist:
                intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_PLAYLIST_ID, 0,0, true, true);
                break;
            default://nothing to do
        }

        if (intent != null) {
            startActivity(intent);
        }

    }
}
