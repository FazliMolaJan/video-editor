package com.luckstro.videooverlay.overlays;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.luckstro.videooverlay.RiGraphicTools;
import com.luckstro.videooverlay.overlays.PlayerInfoOverlayRectangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Austin on 4/18/2017.
 */
public class ScoreOverlayRectangle {
    // Our matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    // Geometric variables
    private static short indices[] = new short[] {0, 1, 2, 0, 2, 3};
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private static float uvs[] = new float[] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };
    private FloatBuffer uvBuffer;

    // Our screenresolution (Or size of the view we are playing the video)
    float mScreenWidth = 1080;
    float mScreenHeight = 607;

    // Misc
    private Context context;
    private int mProgram;

    // number of coordinates per vertex in this array
    private float triangleCoords[];
    private float overlayWidth = 340f / 2f;
    private float overlayHeight = 164f / 2f;

    public ScoreOverlayRectangle(Context context, float screenWidth, float screenHeight) {
        this.context = context;
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
    }

    public void create() {
        createTriangleCoordinates();
        createRectangle();
        setupImage();
        setupOpenGl();
        surfaceChanged(new Float(mScreenWidth).intValue(), new Float(mScreenHeight).intValue());
    }

    private void createTriangleCoordinates() {
        float leftX = mScreenWidth - overlayWidth;
        float rightX = mScreenWidth;
        float upperY = mScreenHeight;
        float lowerY = mScreenHeight - overlayHeight;

        triangleCoords = new float[]{
                leftX, upperY, 0f,
                leftX, lowerY, 0f,
                rightX, lowerY, 0f,
                rightX, upperY, 0f,
        };
    }

    private void createRectangle() {
        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    private void setupOpenGl() {
        // Create the shaders
        int vertexShader = RiGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                RiGraphicTools.vs_SolidColor);
        int fragmentShader = RiGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                RiGraphicTools.fs_SolidColor);

        RiGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(RiGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(RiGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(RiGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executables

        // Create the shaders, images
        vertexShader = RiGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                RiGraphicTools.vs_ImageScore);
        fragmentShader = RiGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                RiGraphicTools.fs_ImageScore);

        RiGraphicTools.sp_Image_ScoreInfo = GLES20.glCreateProgram();
        GLES20.glAttachShader(RiGraphicTools.sp_Image_ScoreInfo, vertexShader);
        GLES20.glAttachShader(RiGraphicTools.sp_Image_ScoreInfo, fragmentShader);
        GLES20.glLinkProgram(RiGraphicTools.sp_Image_ScoreInfo);
    }

    public void surfaceChanged(int width, int height) {
        // We need to know the current width and height.
        mScreenWidth = width;
        mScreenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int)mScreenWidth, (int)mScreenHeight);

        // Clear our matrices
        for(int i=0;i<16;i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
    }

    public void draw() {
        // Render our example
        draw(mtrxProjectionAndView);
    }

    public void draw(float[] m) {
        // Set our shader program
        GLES20.glUseProgram(RiGraphicTools.sp_Image_ScoreInfo);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, PlayerInfoOverlayRectangle.getTexturenames()[1]);

        // get handle to vertex shader's vPosition member
        int mPositionHandle =
                GLES20.glGetAttribLocation(RiGraphicTools.sp_Image_ScoreInfo, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(RiGraphicTools.sp_Image_ScoreInfo,
                "a_texCoord" );

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(RiGraphicTools.sp_Image_ScoreInfo,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (RiGraphicTools.sp_Image_ScoreInfo,
                "s_texture" );

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    private void setupImage() {
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);
    }
}
