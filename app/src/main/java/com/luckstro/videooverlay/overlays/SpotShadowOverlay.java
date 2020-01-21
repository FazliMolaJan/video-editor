package com.luckstro.videooverlay.overlays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.opengl.Image;
import com.luckstro.videooverlay.project.FrameInfo;
import com.luckstro.videooverlay.project.Project;

import java.io.ByteArrayOutputStream;

/**
 * Created by ejdd5cj on 10/30/2017.
 */

public class SpotShadowOverlay implements VideoOverlay {
    private Context context;

    private float viewWidth;
    private float viewHeight;
    private boolean reinitialize = true;
    private float rightX;
    private float lowerY;
    private float midPoint;
    private float gradientStart;
    private float width;
    private float height;
    private float leftX;
    private float upperY;
    private Image image;
    private byte[] imageByteArray;
    private float currentX = 0;
    private float currentY = 0;

    public SpotShadowOverlay(Context context, float leftX, float upperY, float width, float height,
                        float viewWidth, float viewHeight) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.leftX = leftX;
        this.upperY = upperY;

        this.currentX = leftX + viewWidth/2;
        this.currentY = upperY - height/2;

        this.rightX = leftX + width;
        this.lowerY = upperY - height;

        Bitmap sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_circle);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sprite.compress(Bitmap.CompressFormat.PNG, 100, bos);
        this.imageByteArray = bos.toByteArray();
        this.image = new Image(context, leftX, rightX, upperY, lowerY, viewWidth, viewHeight, imageByteArray);
    }

    public void create() {
        image.create();
    }

    public void init() {
    }

    public void draw() {
        FrameInfo currentFrame = Project.currentProject().getCurrentVideoFrame();
        if (currentFrame != null) {
            if ((currentFrame.getY() != currentY) || (currentFrame.getX() != currentX)) {
                currentX = currentFrame.getX();
                currentY = currentFrame.getY();
                Position position = calcuatePosition(currentX, currentY);
                this.image.reposition(position.leftX, position.rightX, position.upperY, position.lowerY);
            }

            image.draw();
        }
    }

    private class Position {
        public float leftX, rightX, upperY, lowerY;
    }

    private Position calcuatePosition(float centerX, float centerY) {
        Position position = new Position();
        position.leftX = centerX - width/2;
        position.rightX = centerX + width/2;
        position.upperY = viewHeight - (centerY - height/2);
        position.lowerY = viewHeight - (centerY + height/2);
        return position;
    }
}
