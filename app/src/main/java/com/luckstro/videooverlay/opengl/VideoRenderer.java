package com.luckstro.videooverlay.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import com.luckstro.videooverlay.overlays.CommentOverlay;
import com.luckstro.videooverlay.overlays.MiniScoreOverlay;
import com.luckstro.videooverlay.overlays.SpotShadowOverlay;
import com.luckstro.videooverlay.overlays.TelestrateOverlay;
import com.luckstro.videooverlay.project.Project;

/**
 * The video renderer will render a frame of video with video
 * overlays drawn on top.
 *
 * Created by Austin on 6/28/2017.
 */

public class VideoRenderer {
    private Context context;
    // Video rectangle is the original video w/o overlay
    private VideoRectangle videoRectangle;

    // VideoOverlays
    private MiniScoreOverlay scoreOverlay;
    private CommentOverlay commentOverlay;
    private SpotShadowOverlay spotShadowOverlay;
    private TelestrateOverlay telestrateOverlay;

    private int mainVideoTextureId;
    private int fontTextureId;
    private boolean playPreVideoOverlays = false;

    public VideoRenderer(Context context, float viewWidth, float viewHeight) {
        this.context = context;
        videoRectangle = new VideoRectangle();

        createScoreOverlay(viewWidth, viewHeight);
        createCommentOverlay(viewWidth, viewHeight);
        createSpotShadowOverlay(viewWidth, viewHeight);
        createTelestrateOverlay(viewWidth, viewHeight);
    }

    /**
     * Initialize the video renderer, including setting textures and
     * initialize overlays.
     */
    public void create() {
        int[] textures = new int[2];

        // Generate main video texture
        GLES20.glGenTextures(2, textures, 0);
        this.mainVideoTextureId = textures[0];
        videoRectangle.setTextureId(this.mainVideoTextureId);
        videoRectangle.create();

        // Generate font texture and add to any overlays which
        // require fonts
        this.fontTextureId = textures[1];
        scoreOverlay.setFontTextureId(this.fontTextureId);
        commentOverlay.setFontTextureId(this.fontTextureId);

        // Create the overlays
        scoreOverlay.create();
        commentOverlay.create();
        spotShadowOverlay.create();
        telestrateOverlay.create();
    }

    public void drawFrame(SurfaceTexture surfaceTexture) {
        // Clear the screen and the buffers.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the main video frame
        videoRectangle.drawFrame(surfaceTexture);

        // Draw The overlays
        if ((Project.currentProject().getAwayTeam().getName() != null)
                && (Project.currentProject().getHomeTeam().getName() != null))
            scoreOverlay.draw();
        if (Project.currentProject().getComment() != null && !"".equals(Project.currentProject().getComment()))
            commentOverlay.draw();
        spotShadowOverlay.draw();
        if (playPreVideoOverlays)
            telestrateOverlay.draw();

        // Tell opengl we are finished.
        GLES20.glFinish();
    }

    public int getMainVideoTextureId() {
        return this.mainVideoTextureId;
    }

    private void createScoreOverlay(float viewWidth, float viewHeight) {
        // Create the score overlay
        float scoreOverlayWidth = 225f;
        float scoreOverlayX = viewWidth - scoreOverlayWidth - 10f;
        float scoreoverlayY = viewHeight - 10f;
        scoreOverlay = new MiniScoreOverlay(context, scoreOverlayX, scoreoverlayY,
                scoreOverlayWidth, 130f, viewWidth, viewHeight);
        Project.currentProject().setMiniScoreOverlay(scoreOverlay);
    }

    private void createCommentOverlay(float viewWidth, float viewHeight) {
        // Create the comment overlay
        boolean useIconForComment = false;
        byte[] icon = null;
        switch (Project.currentProject().getCommentIconType()) {
            case    Player  :   icon = Project.currentProject().getPlayer().getIcon();
                useIconForComment = true;
                break;
            case    None    :   icon = null;
                useIconForComment = false;
                break;
        }
        commentOverlay = new CommentOverlay(context, 0, 150f, viewWidth, 75f, viewWidth,
                viewHeight, useIconForComment, icon);
        Project.currentProject().setCommentOverlay(commentOverlay);
    }

    private void createSpotShadowOverlay(float viewWidth, float viewHeight) {
        // Create the spot shadow overlay
        spotShadowOverlay = new SpotShadowOverlay(context, 0f, 300f, 150f, 100f, viewWidth, viewHeight);
    }

    private void createTelestrateOverlay(float viewWidth, float viewHeight) {
        telestrateOverlay = new TelestrateOverlay(context, viewWidth, viewHeight);
    }

    public void setPlayPreVideoOverlays(boolean playPreVideoOverlays) {
        this.playPreVideoOverlays = playPreVideoOverlays;
    }
}
