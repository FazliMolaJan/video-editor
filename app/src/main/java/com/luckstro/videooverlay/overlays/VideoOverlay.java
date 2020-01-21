package com.luckstro.videooverlay.overlays;

/**
 * A video overlay will display an object over a video frame.
 *
 * Created by Austin on 3/5/2018.
 */

public interface VideoOverlay {
    public void create();
    public void init();
    public void draw();
}
