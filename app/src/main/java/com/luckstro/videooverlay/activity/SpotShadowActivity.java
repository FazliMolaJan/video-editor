package com.luckstro.videooverlay.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.view.CustomVideoView;

import java.io.IOException;

public class SpotShadowActivity extends AppCompatActivity {
    private String videoPath;
    private CustomVideoView videoView;
    private RelativeLayout mainLayout;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telestrator);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("video_path");

        createMediaPlayer();
        createVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
    }

    private void createVideoView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // TODO Replace this with actual sizes read from the video file.
        int width = displayMetrics.widthPixels;
        float videoWidth = 1920;
        float videoHeight = 1080;
        float aspectRatio = videoWidth / videoHeight;
        int height = new Float(width / aspectRatio).intValue();
        // TODO Instead of passing width and height around, have a helper class where it is stored.
        videoView = new CustomVideoView(this, mediaPlayer, width, height, true);
        videoView.setId(View.generateViewId());

        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        mainLayout.addView(videoView, layoutParams);

    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoPath);
        } catch (IOException e) {
            // TODO Handle exception properly.
            e.printStackTrace();
        }
    }
}
