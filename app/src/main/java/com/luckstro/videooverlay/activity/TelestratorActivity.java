package com.luckstro.videooverlay.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.view.CustomVideoView;

import java.io.IOException;

public class TelestratorActivity extends AppCompatActivity {
    private static int OFFSET = 250;
    private String videoPath;
    private CustomVideoView videoView;
    private RelativeLayout mainLayout;
    private MediaPlayer mediaPlayer;
    private LinearLayout videoPanel;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        layoutTheView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        layoutTheView();
    }

    private void layoutTheView() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainLayout.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mainLayout.setLayoutParams(params);
        mainLayout.requestLayout();
    }
//    private void createVideoView() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        // TODO Replace this with actual sizes read from the video file.
//        int width = displayMetrics.widthPixels;
//        float videoWidth = 1920;
//        float videoHeight = 1080;
//        float aspectRatio = videoWidth/videoHeight;
//        int height = new Float(width/aspectRatio).intValue();
//        // TODO Instead of passing width and height around, have a helper class where it is stored.
//        videoView = new CustomVideoView(this, mediaPlayer, width, height, true);
//        videoView.setId(View.generateViewId());
//
//        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
//        mainLayout.addView(videoView, layoutParams);
//
//    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoPath);
        } catch (IOException e) {
            // TODO Handle exception properly.
            e.printStackTrace();
        }
    }

    // TODO This code is duplicated with ProjectConfigurationActivity
    private void createVideoView() {
        Point availableSpace = getDisplayDimensions(this);
        int height = availableSpace.y - OFFSET;
        // TODO Get actual size of video from file.
        float videoWidth = 1920;
        float videoHeight = 1080;
        float aspectRatio = videoWidth/videoHeight;

        int width = new Float(height * aspectRatio).intValue();
        videoView = new CustomVideoView(this, mediaPlayer, width, height, false);
        videoView.setId(View.generateViewId());
        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        videoPanel = (LinearLayout) findViewById(R.id.video_panel);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        videoPanel.addView(videoView, 0, layoutParams);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.button_panel);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, videoPanel.getId());
        linearLayout.setLayoutParams(params);
    }

    private static Point getDisplayDimensions(Context context )
    {
        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics( metrics );
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics( metrics );
        int physicalHeight = metrics.heightPixels;
        int statusBarHeight = getStatusBarHeight( context );
        int navigationBarHeight = getNavigationBarHeight( context );
        int heightDelta = physicalHeight - screenHeight;
        if ( heightDelta == 0 || heightDelta == navigationBarHeight )
        {
            screenHeight -= statusBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }

    private static int getStatusBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    private static int getNavigationBarHeight( Context context )
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }
}
