package com.luckstro.videooverlay.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.luckstro.videooverlay.overlays.CommentOverlay;
import com.luckstro.videooverlay.overlays.ImageOverlay;
import com.luckstro.videooverlay.overlays.MiniScoreOverlay;
import com.luckstro.videooverlay.overlays.SpotShadowOverlay;
import com.luckstro.videooverlay.project.Project;

/**
 * Created by ejdd5cj on 12/2/2017.
 */

public class BitmapRenderer {
    private Context context;
    private float viewWidth;
    private float viewHeight;
    private Bitmap bitmap;
    private VideoRectangle rectangle;
    /**
     * Initialize the rendered.
     *
     * @param context
     */
    public BitmapRenderer(Context context, float viewWidth, float viewHeight, android.graphics.Bitmap bitmap) {
        this.context = context;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        rectangle = new VideoRectangle();
        this.bitmap = new Bitmap(context, 0, 0, viewHeight, viewWidth, viewWidth, viewHeight, bitmap);
    }

    public void create() {
        rectangle.create();
        bitmap.create();
    }

    public void drawFrame() {
        // Clear the screen and the buffers.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        bitmap.draw();

        // Tell opengl we are finished.
        GLES20.glFinish();
    }

    public void setBitmap(android.graphics.Bitmap bitmap) {
        this.bitmap.setBitmap(bitmap);
    }
}
