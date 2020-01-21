package com.luckstro.videooverlay.opengl;

/**
 * Created by ejdd5cj on 6/16/2017.
 */

public class RoundedRectangle {
    private float leftX, topY, rightX, bottomY;
    private Rectangle mainRectangle;

    public RoundedRectangle(float leftX, float rightX, float upperY, float lowerY,
                     float[] topColor, float[] bottomColor, float viewWidth, float viewHeight) {
        mainRectangle = new Rectangle(leftX + .1f, rightX - .1f, upperY, lowerY, topColor,
                bottomColor, viewWidth, viewHeight);
    }

    public void create() {
        mainRectangle.create();
    }

    public void draw() {
        mainRectangle.draw();
    }
}
