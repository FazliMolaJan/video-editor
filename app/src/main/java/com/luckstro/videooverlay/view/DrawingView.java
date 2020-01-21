package com.luckstro.videooverlay.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.luckstro.videooverlay.graphics.DrawingPath;
import com.luckstro.videooverlay.project.Project;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A view that can be drawn on.
 */
public class DrawingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private List<DrawingPath> drawingPaths;
    private DrawingPath currentPath;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // When the size of the view changes, we need to recreate the bitmap to and
        // canvas to match the new size.
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Clear the canvas with transparent so we can see the underlying video.
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // The bitmap contains the existing drawing.  Draw it first, then add the new path to it.
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        Log.i("DrawingView", "Drawing the canvas.");
    }

    /**
     * When the user touches the screen, we want to recognize thier movement and add
     * the path they draw to the exsting drawing.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the touch location
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // The user has just put their finger down.  We need start a new path.
                drawPath.moveTo(touchX, touchY);
                DrawingPath drawingPath = new DrawingPath();
                drawingPath.addPoint(touchX, touchY);
                drawingPaths.add(drawingPath);
                currentPath = drawingPath;
                break;
            case MotionEvent.ACTION_MOVE:
                // The user is moving their finger along the screen.  We need to add to the
                // existing path.
                drawPath.lineTo(touchX, touchY);
                currentPath.addPoint(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                // The path is done drawing.  Draw the path to the canvas, then add the point
                // location to the current path.  The current path will be saved with the
                // project and used when creating the final video.
                drawCanvas.drawPath(drawPath, drawPaint);
                currentPath.addPoint(touchX, touchY);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public List<DrawingPath> getDrawingPaths() {
        return this.drawingPaths;
    }

    //  Create the canvas, path, and color.
    private void setupDrawing(){
        if (this.drawingPaths == null) {
            if (Project.currentProject().getDrawingPaths().get(0L) == null)
                Project.currentProject().getDrawingPaths().put(0L, new ArrayList<DrawingPath>());
            this.drawingPaths = Project.currentProject().getDrawingPaths().get(0L);
        }
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
}
