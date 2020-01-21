package com.luckstro.videooverlay.overlays;

import android.content.Context;

import com.luckstro.videooverlay.entity.ColorConverter;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.opengl.Image;
import com.luckstro.videooverlay.opengl.Rectangle;
import com.luckstro.videooverlay.opengl.TextManager;
import com.luckstro.videooverlay.opengl.TextObject;
import com.luckstro.videooverlay.project.Project;

/**
 * Created by ejdd5cj on 10/19/2017.
 */

public class ImageOverlay {
    //private Rectangle rectangle;
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

    public ImageOverlay(Context context, float leftX, float upperY, float width, float height,
                            float viewWidth, float viewHeight, byte[] image) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.leftX = leftX;
        this.upperY = upperY;

        this.rightX = leftX + width;
        this.lowerY = upperY - height;
        this.midPoint = upperY - (height/2f);
        this.gradientStart = height/4f;

        //rectangle = new Rectangle(leftX, rightX, upperY, lowerY,
        //        //192f/255f
        //        new float[]{ 1f, 1f, 1f, 0f },
        //        new float[]{ 1f, 1f, 1f, 0f }, viewWidth, viewHeight);
        this.imageByteArray = image;
        this.image = new Image(context, leftX, rightX, upperY, lowerY, viewWidth, viewHeight, imageByteArray);
    }

    public void create() {
        //rectangle.create();
        image.create();
    }

    public void init() {
    }

    public void draw() {
        //rectangle.draw();
        image.draw();
    }
}
