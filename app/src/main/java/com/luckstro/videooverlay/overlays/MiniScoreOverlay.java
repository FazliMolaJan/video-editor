package com.luckstro.videooverlay.overlays;

import android.content.Context;

import com.luckstro.videooverlay.entity.ColorConverter;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.opengl.Rectangle;
import com.luckstro.videooverlay.opengl.TextManager;
import com.luckstro.videooverlay.opengl.TextObject;
import com.luckstro.videooverlay.project.Project;

/**
 * Created by Austin on 6/7/2017.
 */

public class MiniScoreOverlay implements VideoOverlay {
    private static final float BORDER_LEFT_RIGHT = 5f;
    private static final float BORDER_TOP_BOTTOM = 5f;
    private static float HEIGHT_PERCENTAGE_OF_SURFACE = .112360f * 4;
    private Rectangle outerRectangle;
    private Rectangle awayTeamRectangle0;
    private Rectangle awayTeamRectangle1;
    private Rectangle homeTeamRectangle0;
    private Rectangle homeTeamRectangle1;
    private TextManager awayTeamAbbreviation;
    private TextManager homeTeamAbbreviation;
    private TextManager awayTeamScore;
    private TextManager homeTeamScore;
    private float ssu; // TODO Figure out what ssu is for.
    private int fontTextureId;
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

    public MiniScoreOverlay(Context context, float leftX, float upperY, float width, float height,
                            float viewWidth, float viewHeight) {
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
                new float[]{ 1f, 1f, 1f, 1.0f },
                new float[]{ 1f, 1f, 1f, 1.0f }, viewWidth, viewHeight);
        awayTeamRectangle0 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                upperY - BORDER_TOP_BOTTOM, midPoint + gradientStart,
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                viewWidth, viewHeight);
        awayTeamRectangle1 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint + gradientStart, midPoint,
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                new float[]{ 0f, 0f, 0f, 1.0f }, viewWidth, viewHeight);
        homeTeamRectangle0 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint, midPoint - gradientStart,
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()),
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()), viewWidth, viewHeight);
        homeTeamRectangle1 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint - gradientStart, lowerY + BORDER_TOP_BOTTOM,
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()),
                new float[]{ 0f, 0f, 0f, 1.0f }, viewWidth, viewHeight);
        setupScaling();
    }

    private void setupScaling() {
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
        awayTeamRectangle0.create();
        awayTeamRectangle1.create();
        homeTeamRectangle0.create();
        homeTeamRectangle1.create();
    }

    public void init() {
       reinitialize = true;
    }

    // TODO This isn't the correct way to do this.  Fix so the score and team names can be dynamic but we don't need to reinitialize on every draw()
    private void reinit() {
        // TODO Clean this up
        setupAwayText();
        setupHomeText();
        setupAwayScore();
        setupHomeScore();
        awayTeamAbbreviation.create();
        homeTeamAbbreviation.create();
        awayTeamScore.create();
        homeTeamScore.create();
        homeTeamRectangle0.create();
        homeTeamRectangle1.create();
        awayTeamRectangle0.create();
        awayTeamRectangle1.create();
    }

    public void draw() {
        if (reinitialize) {
            reinit();
            reinitialize = false;
        }
        outerRectangle.draw();
        awayTeamRectangle0.draw();
        awayTeamRectangle1.draw();
        homeTeamRectangle0.draw();
        homeTeamRectangle1.draw();

        awayTeamAbbreviation.draw();
        homeTeamAbbreviation.draw();
        awayTeamScore.draw();
        homeTeamScore.draw();
    }

    private void setupAwayText() {
        // Create our text manager
        awayTeamAbbreviation = new TextManager(context, viewWidth, viewHeight);

        // Tell our text manager to use index 1 of textures loaded
        awayTeamAbbreviation.setTextureID(this.fontTextureId);

        // Pass the uniform scale
        awayTeamAbbreviation.setUniformscale(ssu);

        String awayTeamLabel = "";

        Team awayTeam = Project.currentProject().getAwayTeam();
        awayTeamLabel = awayTeam.getAbbreviation();

        // Create our new textobject
        TextObject txt = new TextObject(awayTeamLabel, leftX + 15f, upperY - 55f);

        // Add it to our manager
        awayTeamAbbreviation.addText(txt);

        // Prepare the text for rendering
        awayTeamAbbreviation.prepareDraw();
    }

    private void setupAwayScore() {
        // TODO Clean up
        awayTeamRectangle0 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                upperY - BORDER_TOP_BOTTOM, midPoint + gradientStart,
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                viewWidth, viewHeight);
        awayTeamRectangle1 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint + gradientStart, midPoint,
                ColorConverter.getColorArray(Project.currentProject().getAwayTeam().getColor()),
                new float[]{ 0f, 0f, 0f, 1.0f }, viewWidth, viewHeight);

        // Create our text manager
        awayTeamScore = new TextManager(context, viewWidth, viewHeight);

        // Tell our text manager to use index 1 of textures loaded
        awayTeamScore.setTextureID(this.fontTextureId);

        // Pass the uniform scale
        awayTeamScore.setUniformscale(ssu);

        String teamScoreLabel = "";

        teamScoreLabel = String.valueOf(Project.currentProject().getAwayTeamScore());

        // Create our new textobject
        // We adjust the leftX so that we are right justified.
        TextObject txt = new TextObject(teamScoreLabel, leftX + 175f - (TextManager.RI_TEXT_WIDTH/2 * (teamScoreLabel.length() - 1)), upperY - 55f);

        // Add it to our manager
        awayTeamScore.addText(txt);

        // Prepare the text for rendering
        awayTeamScore.prepareDraw();
    }

    private void setupHomeText() {
        // Create our text manager
        homeTeamAbbreviation = new TextManager(context, viewWidth, viewHeight);

        // Tell our text manager to use index 1 of textures loaded
        homeTeamAbbreviation.setTextureID(this.fontTextureId);

        // Pass the uniform scale
        homeTeamAbbreviation.setUniformscale(ssu);

        String teamLabel = "";

        Team team= Project.currentProject().getHomeTeam();
        teamLabel = team.getAbbreviation();

        // Create our new textobject
        TextObject txt = new TextObject(teamLabel, leftX + 15f, upperY - 115f);

        // Add it to our manager
        homeTeamAbbreviation.addText(txt);

        // Prepare the text for rendering
        homeTeamAbbreviation.prepareDraw();
    }

    private void setupHomeScore() {
        // TODO Clean Up
        homeTeamRectangle0 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint, midPoint - gradientStart,
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()),
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()), viewWidth, viewHeight);
        homeTeamRectangle1 = new Rectangle(leftX + BORDER_LEFT_RIGHT, rightX - BORDER_LEFT_RIGHT,
                midPoint - gradientStart, lowerY + BORDER_TOP_BOTTOM,
                ColorConverter.getColorArray(Project.currentProject().getHomeTeam().getColor()),
                new float[]{ 0f, 0f, 0f, 1.0f }, viewWidth, viewHeight);

        // Create our text manager
        homeTeamScore = new TextManager(context, viewWidth, viewHeight);

        // Tell our text manager to use index 1 of textures loaded
        homeTeamScore.setTextureID(this.fontTextureId);

        // Pass the uniform scale
        homeTeamScore.setUniformscale(ssu);

        String teamScoreLabel = "";

        teamScoreLabel = String.valueOf(Project.currentProject().getHomeTeamScore());

        // Create our new textobject
        // We adjust the leftX so that we are right justified.
        TextObject txt = new TextObject(teamScoreLabel, leftX + 175f - (TextManager.RI_TEXT_WIDTH/2 * (teamScoreLabel.length() - 1)), upperY - 115f);

        // Add it to our manager
        homeTeamScore.addText(txt);

        // Prepare the text for rendering
        homeTeamScore.prepareDraw();
    }

    public void setFontTextureId(int fontTextureId) {
        this.fontTextureId = fontTextureId;
    }
}
