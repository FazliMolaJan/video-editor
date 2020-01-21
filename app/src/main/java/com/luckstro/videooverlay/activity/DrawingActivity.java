package com.luckstro.videooverlay.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.adapter.VideoGridViewAdapter;
import com.luckstro.videooverlay.project.Project;
import com.luckstro.videooverlay.view.CustomVideoView;
import com.luckstro.videooverlay.view.DrawingSurfaceView;
import com.luckstro.videooverlay.view.DrawingView;

import java.io.IOException;

/**
 * The Drawing activity allows the user to draw directly onto the video.  This version
 * of the drawing activity only allows the user to draw onto the beginning of the video.  You
 * can not draw in the middle of the video.
 */
public class DrawingActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =001010;
    private static int OFFSET = 250;
    private CustomVideoView videoView;
    private ConstraintLayout mainLayout;
    private MediaPlayer mediaPlayer;
    private DrawingView drawView;
    private String videoPath;
    private RelativeLayout videoPanel;
    private DrawingSurfaceView drawingSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("video_path");

        createMediaPlayer();
        createVideoView();

        //Bring the drawing view to the front so it's the view the user touches.
        drawView.bringToFront();

        // The play button will start the drawing animation.
        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bring the surface view to the front and make it visible.
                drawingSurfaceView.setVisibility(View.VISIBLE);
                drawingSurfaceView.bringToFront();
                drawingSurfaceView.setDrawingPaths(drawView.getDrawingPaths());
                // Toggle which buttons are visible.
                findViewById(R.id.play_button).setVisibility(View.GONE);
                findViewById(R.id.pause_button).setVisibility(View.VISIBLE);
                drawingSurfaceView.startDrawingAnimation();
            }
        });

        // TODO This should be replaced with a stop button.
        // To simplify things, just start and stop the drawing.
        findViewById(R.id.pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingSurfaceView.stopDrawingAnimation();
                // Hide the surface view and bring the draw view back to the front.
                drawingSurfaceView.setVisibility(View.INVISIBLE);
                drawView.bringToFront();
                // Toggle which buttons are visible.
                findViewById(R.id.play_button).setVisibility(View.VISIBLE);
                findViewById(R.id.pause_button).setVisibility(View.GONE);
            }
        });

        // We need to permission to write to the disk so we can create a temporary
        // video of the drawing, which we will add to the main video.
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else {

        }
    }

    // TODO We need to handle the permissions better.  This really doesn't do anything
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO Handle permission request better.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainLayout.getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        mainLayout.setLayoutParams(params);
//        mainLayout.requestLayout();
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
//        videoView = new CustomVideoView(this, mediaPlayer, width, height, false);
//        videoView.setId(View.generateViewId());
//        mainLayout = (ConstraintLayout) findViewById(R.id.drawing_main);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
//        mainLayout.addView(videoView, layoutParams);
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
        videoPanel = (RelativeLayout) findViewById(R.id.telestrate_panel);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        videoPanel.addView(videoView, 0, layoutParams);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.button_panel);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, videoPanel.getId());
        linearLayout.setLayoutParams(params);

        drawingSurfaceView = new DrawingSurfaceView(this);
        drawingSurfaceView.setId(View.generateViewId());
        drawingSurfaceView.setVisibility(View.INVISIBLE);
        SurfaceHolder holder = drawingSurfaceView.getHolder();
        holder.addCallback(drawingSurfaceView);
        videoPanel.addView(drawingSurfaceView, 0, layoutParams);

        drawView = new DrawingView(this);
        drawView.setId(View.generateViewId());
        videoPanel.addView(drawView, 0, layoutParams);
    }

    // TODO Move this to a helper class so it can be used anywhere.
    private static Point getDisplayDimensions(Context context ) {
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
        if ( heightDelta == 0 || heightDelta == navigationBarHeight ) {
            screenHeight -= statusBarHeight;
        }

        return new Point( screenWidth, screenHeight );
    }

    // TODO Move this to a helper class so it can be used anywhere.
    private static int getStatusBarHeight( Context context ) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }

    // TODO Move this to a helper class so it can be used anywhere.
    private static int getNavigationBarHeight( Context context ) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
    }
}
