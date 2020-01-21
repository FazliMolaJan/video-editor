package com.luckstro.videooverlay.overlays;

import android.content.Context;

import com.luckstro.videooverlay.entity.ColorConverter;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.opengl.Rectangle;
import com.luckstro.videooverlay.opengl.TextManager;
import com.luckstro.videooverlay.opengl.TextObject;
import com.luckstro.videooverlay.project.Project;

/**
 * Created by ejdd5cj on 10/12/2017.
 */

public class CommentOverlay implements VideoOverlay {
    private float ssu; // TODO Figure out what ssu is for.
    private int fontTextureId;
    private Context context;
    private Rectangle outerRectangle;
    private ImageOverlay imageOverlay;
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
    private boolean useIcon;

    public CommentOverlay(Context context, float leftX, float upperY, float width, float height,
                            float viewWidth, float viewHeight, boolean useIcon, byte[] icon) {
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

        outerRectangle = new Rectangle(leftX, rightX, upperY, lowerY,
                //192f/255f
                new float[]{ 0f, 0f, 0f, 1.0f },
                new float[]{ 0f, 0f, 0f, 1.0f }, viewWidth, viewHeight);

        this.useIcon = useIcon;
        if (useIcon)
            imageOverlay = new ImageOverlay(context, leftX + 25, upperY + 12, 100f, 100f,
                    viewWidth, viewHeight, icon);

        setupScaling();
    }

    public void setupScaling() {
        // The screen resolutions
        float swp = 1280f;
        float shp = 720f;

        // Orientation is assumed portrait
        float ssx = swp / 320.0f;
        float ssy = shp / 480.0f;

        // Get our uniform scaler
        if(ssx > ssy)
            ssu = ssy;
        else
            ssu = ssx;
    }

    public void create() {
        outerRectangle.create();
        if (useIcon)
            imageOverlay.create();
    }

    public void init() {
        reinitialize = true;
    }

    // TODO This isn't the correct way to do this.  Fix so the comment can be dynamic and we don't need to reinitialize on every draw()
    private void reinit() {
        // TODO Clean this up
        setupText();
        comment.create();
    }

    public void draw() {
        if (reinitialize) {
            reinit();
            reinitialize = false;
        }
        outerRectangle.draw();
        comment.draw();
        if (useIcon)
            imageOverlay.draw();
    }

    private TextManager comment;

    private void setupText() {
        // Create our text manager
        comment = new TextManager(context, viewWidth, viewHeight);

        // Tell our text manager to use index 1 of textures loaded
        comment.setTextureID(this.fontTextureId);

        // Pass the uniform scale
        comment.setUniformscale(ssu);

        String label = Project.currentProject().getComment();

        float offset = 25f;
        if (useIcon)
            offset += 125f;
        // Create our new textobject
        TextObject txt = new TextObject(label, leftX + offset, upperY - 60f);

        // Add it to our manager
        comment.addText(txt);

        // Prepare the text for rendering
        comment.prepareDraw();
    }

    public void setFontTextureId(int fontTextureId) {
        this.fontTextureId = fontTextureId;
    }
}
