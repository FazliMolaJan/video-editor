package com.luckstro.videooverlay.view;

import java.io.IOException;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.opengl.TextureRender;
import com.luckstro.videooverlay.opengl.VideoRenderer;
import com.luckstro.videooverlay.project.FrameInfo;
import com.luckstro.videooverlay.project.Project;

public class CustomVideoView extends GLSurfaceView implements View.OnTouchListener {
        private static final String TAG = "CustomVideoView";
        private static final int SLEEP_TIME_MS = 1000;
        VideoRender renderer;
        private MediaPlayer mediaPlayer = null;
        private boolean constantTouchUpdate;

    public CustomVideoView(Context context, MediaPlayer mp, float screenWidth, float screenHeight,
                           boolean constantTouchUpdate) {
        super(context);
        setEGLContextClientVersion(2);
        mediaPlayer = mp;
        init(context, screenWidth, screenHeight);
        setOnTouchListener(this);
        this.constantTouchUpdate = constantTouchUpdate;
    }

    public void init(Context context, float screenWidth, float screenHeight) {
        renderer = new VideoRender(context, screenWidth, screenHeight);
        setRenderer(renderer);
    }

    @Override
    public void onResume() {
        queueEvent(new Runnable(){
                public void run() {
                    renderer.setMediaPlayer(mediaPlayer);
                }});
        super.onResume();
    }

    public void startTest() throws Exception {
        Thread.sleep(SLEEP_TIME_MS);
        mediaPlayer.start();
        Thread.sleep(SLEEP_TIME_MS * 5);
        mediaPlayer.setSurface(null);
        while (mediaPlayer.isPlaying()) {
            Thread.sleep(SLEEP_TIME_MS);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Remove this, it is only for testing
//        if (this.constantTouchUpdate == false) {
//            if (!mediaPlayer.isPlaying())
//                playVideo();
//        } else {
//                // TODO implement full video player (play, pause, FF, etc.)
////                try {
////                    if (mediaPlayer.isPlaying())
////                        mediaPlayer.pause();
////                    else {
////                        playVideo();
////                    }
////                } catch (Exception e) {
////                    // TODO handle exception correctly.
////                    e.printStackTrace();
////                }
//            }
        if (!mediaPlayer.isPlaying())
            playVideo();
        renderer.setTouchLocation(event.getX(), event.getY());
        return true;
    }

    public void playVideo() {
        mediaPlayer.start();
        renderer.setDrawFrames(this.constantTouchUpdate);
        renderer.setPlayPreVideoContent(true);
    }

    /**
     * A GLSurfaceView implementation that wraps TextureRender.  Used to render frames from a
     * video decoder to a View.
     */
    private static class VideoRender
            implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
        private static String TAG = "VideoRender";
        private VideoRenderer renderer;
        private SurfaceTexture activeSurfaceTexture;
        private boolean updateSurface = false;
        private MediaPlayer mediaPlayer;
        private boolean drawFrames = false;
        private float touchX;
        private float touchY;
        private boolean playPreVideoContent = false;

        public VideoRender(Context context, float screenWidth, float screenHeight) {
            renderer = new VideoRenderer(context, screenWidth, screenHeight);
        }

        public void setMediaPlayer(MediaPlayer player) {
            mediaPlayer = player;
        }

        public void onDrawFrame(GL10 glUnused) {
            synchronized(this) {
                if (updateSurface) {
                    activeSurfaceTexture.updateTexImage();
                    updateSurface = false;
                }
            }

            int time = mediaPlayer.getCurrentPosition();

            if (drawFrames) {
                // TODO if video has looped around and restarted, clear out existing frame info.
                Project.currentProject().addFrameInfo(time,
                        new FrameInfo(this.touchX, this.touchY));
            }

            Project.currentProject().setCurrentVideoFrame(time);
//            FrameInfo frameInfo = Project.currentProject().getNearestFrame(time);
//            if (frameInfo != null)
//                Log.i("CustomVideoView", "Frame: " + time + " X: " + frameInfo.getX() + " Y: " + frameInfo.getY());
//            else
//                Log.i("CustomVideoView", "Frame: " + time + " is null");

            renderer.drawFrame(activeSurfaceTexture);
        }

        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        }

        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
            renderer.create();
            /*
             * Create the SurfaceTexture that will feed this textureID,
             * and pass it to the MediaPlayer
             */
            activeSurfaceTexture = createMediaPlayerSurface();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }
            synchronized(this) {
                updateSurface = false;
            }
        }

        private SurfaceTexture createMediaPlayerSurface() {
            SurfaceTexture surfaceTexture = new SurfaceTexture(renderer.getMainVideoTextureId());
            surfaceTexture.setOnFrameAvailableListener(this);
            Surface surface = new Surface(surfaceTexture);
            mediaPlayer.setSurface(surface);
            surface.release();
            return surfaceTexture;
        }

        long lastTime = 0;

        synchronized public void onFrameAvailable(SurfaceTexture surface) {
            // TODO If the project has a telestration at the beginning of the video, then repeatedly reset
            // the frame back to the first frame until the telestration animation is done playing.
            // ex:
            // if (animation not done)
            //      mediaPlayer.seekTo(0)
            // This will keep the video at the first frame until the animation is complete.

//            Log.i("CustomVideoView", "Current Media Player Position: " + mediaPlayer.getCurrentPosition());
//            Date date = new Date();
//            Log.i("CustomVideoView", "Current Time: " + date.getTime() + " Last Time: " + lastTime);
//            if (date.getTime() >= (lastTime + 100)) {
//                lastTime = date.getTime();
                updateSurface = true;
//            }
        }

        public void setDrawFrames(boolean drawFrames) {
            this.drawFrames = drawFrames;
        }

        public void setTouchLocation(float x, float y) {
            this.touchX = x;
            this.touchY = y;
        }

        public void setPlayPreVideoContent(boolean playPreVideoContent) {
            renderer.setPlayPreVideoOverlays(true);
        }
    }
}

