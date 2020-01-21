package com.luckstro.videooverlay.graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejdd5cj on 11/21/2017.
 */

public class DrawingPath {
    public List<DrawingPathPoint> points = new ArrayList<>();

    public void addPoint(float x, float y) {
        points.add(new DrawingPathPoint(x, y));
    }

    public List<DrawingPathPoint> getPoints() {
        return points;
    }
}
