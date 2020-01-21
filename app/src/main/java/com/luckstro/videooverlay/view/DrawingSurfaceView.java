package com.luckstro.videooverlay.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.GridView;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.activity.VideosBrowserActivity;
import com.luckstro.videooverlay.adapter.VideoGridViewAdapter;
import com.luckstro.videooverlay.graphics.DrawingPath;
import com.luckstro.videooverlay.graphics.DrawingPathPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Surface view which animates a list of drawing paths on a transparent view.
 *
 */
public class DrawingSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback {
    private Context context;
    private DrawingSurfaceViewThread thread;
    private SurfaceHolder sh;
    private final Paint pen = new Paint();
    private List<DrawingPath> drawingPaths;

    public DrawingSurfaceView(Context context) {
        super(context);
        this.context = context;
        sh = getHolder();
        sh.addCallback(this);
        // Make the surface holder transparent so the background displays through.
        sh.setFormat(PixelFormat.TRANSPARENT);
        setFocusable(true);

        // TODO Allow the color to be changed.
        // Create the pen we will draw with
        pen.setColor(0xFF660000);
        pen.setAntiAlias(true);
        pen.setStrokeWidth(20);
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeJoin(Paint.Join.ROUND);
        pen.setStrokeCap(Paint.Cap.ROUND);

        // Make the background transparent so the image below displays through.
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Run the drawing in a separate thread to improve performance.
        thread = new DrawingSurfaceViewThread(sh, null, new Handler());
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void startDrawingAnimation() {
        thread.setRunning(true);
        thread.start();
    }

    public void stopDrawingAnimation() {
        thread.setRunning(false);
    }

    /**
     * Sets the list of drawing paths to animate to the screen.  The paths will be animated
     * in the order they are in the list.
     *
     * @param drawingPaths
     */
    public void setDrawingPaths(List<DrawingPath> drawingPaths) {
        this.drawingPaths = drawingPaths;
    }

    /**
     * A thread to draw the surface view animation
     */
    private class DrawingSurfaceViewThread extends Thread {
        private int canvasWidth = 200;
        private int canvasHeight = 400;
        private static final int SPEED = 2;
        private boolean run = false;

        public DrawingSurfaceViewThread(SurfaceHolder surfaceHolder, Context context,
                            Handler handler) {
            sh = surfaceHolder;
        }

        public void doStart() {
            synchronized (sh) {
            }
        }

        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = sh.lockCanvas(null);
                    synchronized (sh) {
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        sh.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (sh) {
                canvasWidth = width;
                canvasHeight = height;
                doStart();
            }
        }

        int lastDrawPath = 0;
        int lastDrawPoint = 0;
        Path path = new Path();

        /**
         * Draw the animation.  The surface holder should be locked before calling the draw.
         * @param canvas
         */
        private void doDraw(Canvas canvas) {
            if (lastDrawPath < drawingPaths.size()) {
                // We have a path to draw, so create a bitmap to draw the canvas to.
                // The bitmapCanvas will draw the animation to a bitmap which can save and
                // possibly use later.
                // The regular canvas, just named canvas, is what is actually visible on the screen.
                Bitmap bitmap =
                        Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
                Canvas bitmapCanvas = new Canvas(bitmap);
                Log.i("DrawingView", "Drawing the canvas.  Last Draw Path : " + lastDrawPath);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                // Get the path we are drawing.
                // If lastDrawPoint is 0 it means we are working on a new path, so we pick the
                // pen up and move it to the new location (moveTo).  If it is not 0, then we are continuing
                // on the same path and we keep out pen down (lineTo)
                DrawingPath drawingPath = drawingPaths.get(lastDrawPath);
                if (lastDrawPoint == 0) {
                    path.moveTo(drawingPath.getPoints().get(lastDrawPoint).getX(), drawingPath.getPoints().get(lastDrawPoint).getY());
                } else
                    path.lineTo(drawingPath.getPoints().get(lastDrawPoint).getX(), drawingPath.getPoints().get(lastDrawPoint).getY());

                // Increase the lastDrawPoint.  If it's greater than the # of points on the path
                // Then we are done with the path, so we reset the lastDrawPoint and move to the
                // next drawing path.
                lastDrawPoint++;
                if (lastDrawPoint >= drawingPath.getPoints().size()) {
                    lastDrawPoint = 0;
                    lastDrawPath++;
                }

                // Draw the path to the canvas and save the canvas.
                canvas.drawPath(path, pen);
                bitmapCanvas.drawPath(path, pen);
                canvas.save();
                bitmapCanvas.save();
            } else {
                // There are no more paths to draw, so clear the canvas and just draw the last path
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawPath(path, pen);
                canvas.save();
                setRunning(false);
            }
        }
    }
}
