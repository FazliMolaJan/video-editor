package com.luckstro.videooverlay.adapter;

import android.graphics.Bitmap;

/**
 * Created by ejdd5cj on 7/12/2017.
 */

public class ImageItem {
    private Bitmap image;

    public ImageItem(Bitmap image) {
        super();
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
