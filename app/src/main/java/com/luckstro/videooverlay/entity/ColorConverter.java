package com.luckstro.videooverlay.entity;


import com.luckstro.videooverlay.R;

/**
 * Created by ejdd5cj on 9/29/2017.
 */

public class ColorConverter {
    public static float[] getColorArray(int color) {
        switch(color) {
            case R.color.red       :        return new float[] {.77f, .04f, .19f, 1f};
            case R.color.darkblue  :       return new float[] {0f, 0.15f, .36f, 1f};
            case R.color.green      :       return new float[] {.0f, .26f, .21f, 1f};
            case R.color.silver  :       return new float[] {.72f, .72f, .72f, 1f};
            case R.color.lightblue  :       return new float[] {.73f, .87f, .97f, 1f};
            case R.color.blue  :       return new float[] {.02f, .32f, .62f, 1f};
            case R.color.yellow  :       return new float[] {.99f, .81f, .13f, 1f};
            case R.color.orange  :       return new float[] {.95f, .47f, .14f, 1f};
            case R.color.gold  :       return new float[] {.82f, .72f, .48f, 1f};
            case R.color.purple  :       return new float[] {.23f, .13f, .56f, 1f};
            case R.color.white  :       return new float[] {1f, 1f, 1f, 1f};
            case R.color.black  :   return new float[] {0f, 0f, 0f, 1f};
        }
        return new float[] {0f, 0f, 0f, 1f};
    }
}
