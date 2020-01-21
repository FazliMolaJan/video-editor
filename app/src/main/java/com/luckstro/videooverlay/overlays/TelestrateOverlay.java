package com.luckstro.videooverlay.overlays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.Log;

import com.luckstro.videooverlay.graphics.DrawingPath;
import com.luckstro.videooverlay.opengl.Image;
import com.luckstro.videooverlay.project.FrameInfo;
import com.luckstro.videooverlay.project.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Austin on 3/5/2018.
 */

public class TelestrateOverlay implements VideoOverlay {
    private int currentFrame;
    private List<DrawingPath> drawingPaths;
    private int lastDrawPath = 0;
    private int lastDrawPoint = 0;
    private Path path = new Path();
    private final Paint pen = new Paint();
    private int viewWidth;
    private int viewHeight;
    private Image image;
    private byte[] imageByteArray;

    public TelestrateOverlay(Context context, float viewWidth, float viewHeight) {
        this.viewWidth = new Float(viewWidth).intValue();
        this.viewHeight = new Float(viewHeight).intValue();

        // TODO We are initializing the image object here, but it will need to be recreated with
        // each call since the image will be changing.
        this.image = new Image(context, 0, viewWidth, 0, viewHeight, viewWidth, viewHeight, createEmptyBitmap());
    }

    @Override
    public void create() {
        // TODO Allow the color to be changed.
        // Create the pen we will draw with
        pen.setColor(0xFF660000);
        pen.setAntiAlias(true);
        pen.setStrokeWidth(20);
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeJoin(Paint.Join.ROUND);
        pen.setStrokeCap(Paint.Cap.ROUND);
        image.create();
    }

    @Override
    public void init() {
        // Reset the current telestration frame to 0
        currentFrame = 0;
    }

    @Override
    public void draw() {
        // Draw the current telestration frame by creating a bit map
        // from the path.  Then increment the telestrator count

        // Create a bitmap and canvas.
        // Draw the bitmap w/ open gl
        // Destroy the bitmap?

        // Currently we only support telestration at the beginning of the video,
        // so we can set the drawing paths to the drawing path at time 0.
        drawingPaths = Project.currentProject().getDrawingPaths().get(0L);

        // TODO Instead of drawing to a bitmap, try recording drawing with the picture object to see if performance is better.
        if ((drawingPaths != null) && (lastDrawPath < drawingPaths.size())) {
            // We have a path to draw, so create a bitmap to draw the canvas to.
            // The bitmapCanvas will draw the animation to a bitmap which can save and
            // possibly use later.
            Bitmap bitmap =
                    Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            Log.i("DrawingView", "Drawing the canvas.  Last Draw Path : " + lastDrawPath);
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
            bitmapCanvas.drawPath(path, pen);
            bitmapCanvas.save();
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/sign.png");
//                FileOutputStream out = new FileOutputStream("sign.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            image.resetBitmap(bitmap);
            image.draw();
        }
    }

    private byte[] createEmptyBitmap() {
        Bitmap bitmap =
                Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        Log.i("DrawingView", "Drawing the canvas.  Last Draw Path : " + lastDrawPath);
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        bitmapCanvas.save();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }
}
