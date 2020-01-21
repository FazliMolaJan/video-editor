package com.luckstro.videooverlay.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ejdd5cj on 6/28/2017.
 */

public class VideoRectangle {
    private static final String TAG = "VideoRectangle";
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +      // highp here doesn't seem to matter
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private final float[] triangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,  1.0f, 0, 1.f, 1.f,
    };

    private float[] modelViewProjectionMatrix = new float[16];
    private float[] transformationMatrix = new float[16];

    private FloatBuffer triangleVertices;

    private int programId;
    private int textureId;
    private int uniformModelViewProjectionMatrixHandle;
    private int uniformTransformationMatrixHandle;
    private int positionHandle;
    private int textureHandle;

    public VideoRectangle() {
        // Allocate memory and store the triangle vertices in a buffer.
        triangleVertices = ByteBuffer.allocateDirect(
                triangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangleVertices.put(triangleVerticesData).position(0);

        // Sets the transformationMatrix to "1".
        // [1, 0, 0, 0
        //  0, 1, 0, 0
        //  0, 0, 1, 0
        //  0, 0, 0, 1]
        // This is done to have a starting point for the transformationMarix.
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    public void create() {
        programId = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (programId == 0) {
            throw new RuntimeException("failed creating program");
        }
        positionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        OpenGLUtil.checkGlError(TAG, "glGetAttribLocation aPosition");
        if (positionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        textureHandle = GLES20.glGetAttribLocation(programId, "aTextureCoord");
        OpenGLUtil.checkGlError(TAG, "glGetAttribLocation aTextureCoord");
        if (textureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        uniformModelViewProjectionMatrixHandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");
        OpenGLUtil.checkGlError(TAG, "glGetUniformLocation uMVPMatrix");
        if (uniformModelViewProjectionMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        uniformTransformationMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        OpenGLUtil.checkGlError(TAG, "glGetUniformLocation uSTMatrix");
        if (uniformTransformationMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        OpenGLUtil.checkGlError(TAG, "glBindTexture mTextureID");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        OpenGLUtil.checkGlError(TAG, "glTexParameter");
    }

    /**
     * Called for each frame to draw it to the surface texture.
     *
     * @param st
     */
    public void drawFrame(SurfaceTexture st) {
        OpenGLUtil.checkGlError(TAG, "onDrawFrame start");
        st.getTransformMatrix(transformationMatrix);

        // Tell opengl which program to use.
        GLES20.glUseProgram(programId);
        OpenGLUtil.checkGlError(TAG, "glUseProgram");

        // Specify which texture unit to make active and bind the textureId to the
        // target texture GL_TEXTURE_EXTERNAL_OES.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        // Position the buffer to the data position of the triangle vertices and
        // set it as the positionHandle.
        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        OpenGLUtil.checkGlError(TAG, "glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        OpenGLUtil.checkGlError(TAG, "glEnableVertexAttribArray maPositionHandle");

        // Position the buffer to the UV position of the triangle vertices and
        // set it as the textureHandle.
        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        OpenGLUtil.checkGlError(TAG, "glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(textureHandle);
        OpenGLUtil.checkGlError(TAG, "glEnableVertexAttribArray maTextureHandle");

        // Reset the ModelViewProjection matrix to "1" and set the uniform matrixs.
        Matrix.setIdentityM(modelViewProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniformModelViewProjectionMatrixHandle, 1, false,
                modelViewProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniformTransformationMatrixHandle, 1, false,
                transformationMatrix, 0);

        // Draw the arrays (textures) to the screen.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        OpenGLUtil.checkGlError(TAG, "glDrawArrays");
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        // Load the vertex shader source code
        int vertexShader = OpenGLUtil.createShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        // Load the fragment shader source code
        int pixelShader = OpenGLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        // Create a new program and attach the shaders.
        int program = GLES20.glCreateProgram();
        OpenGLUtil.checkGlError(TAG, "glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        OpenGLUtil.checkGlError(TAG, "glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        OpenGLUtil.checkGlError(TAG, "glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
}
