package com.luckstro.videooverlay.graphics;

import android.graphics.Bitmap;

/**
 * Created by ejdd5cj on 10/9/2017.
 */

public class ImageTool {
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
