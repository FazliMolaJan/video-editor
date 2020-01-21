package com.luckstro.videooverlay.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by ejdd5cj on 6/11/2017.
 */

public class Rectangle {
    private static final String TAG = "Rectangle";

    /** How many bytes per float. */
    private static final int BYTES_PER_FLOAT = 4;

    /** How many bytes per vertex. */
    private static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;

    /** Offset of the position data. */
    private static final int POSITION_OFFSET = 0;

    /** Size of the position data in elements. */
    private static final int NUMBER_OF_COORDS_IN_POSITION = 3;

    /** Offset of the color data. */
    private static final int COLOR_OFFSET = 3;

    /** Size of the color data in elements. */
    private static final int NUMBER_OF_COORDS_IN_COLOR = 4;

    /** The number of coordinates per vertex in the rectangle. */
    private static final int COORDS_PER_VERTEX = 7;

    /** The order we want to draw the indices.  Since OpenGL actually just draws
     * triangles, we will get it to draw two triangles which connect together to
     * form a rectangle. */
    private static short[] INDICES_ORDER = new short[] {0, 1, 2, 0, 2, 3};

    /** This shader will set the color and position of the rectangle.  It position
        of the vertexs will not change with this shader. */
    private static final String STATIC_VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;       \n"
            + "attribute vec4 a_Position;     \n"         // Per-vertex position information we will pass in.
            + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
            + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

            + "void main() {                  \n"
            + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
            + "   gl_Position = uMVPMatrix * a_Position;   \n"       // Pass the position through to the fragement shader.
            + "}                              \n";

    /** This fragment shader will pass the color on through to opengl.  The color
        will not change with this shader. */
    private static final String STATIC_FRAGMENT_SHADER_CODE =
            "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                                                    // precision in the fragment shader.
            + "varying vec4 v_Color;          \n"	// This is the color from the vertex shader interpolated across the
                                                    // triangle per fragment.
            + "void main() {                  \n"
            + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
            + "}                              \n";


    private float coords[];
    private ShortBuffer drawListBuffer;
    private FloatBuffer vertexBuffer;
    private int programId;

    // Our matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    private float viewWidth;
    private float viewHeight;

    public Rectangle(float leftX, float rightX, float upperY, float lowerY,
                     float[] topColor, float[] bottomColor, float viewWidth, float viewHeight) {
        coords = new float[] {
                leftX, lowerY, 0f,
                bottomColor[0], bottomColor[1], bottomColor[2], bottomColor[3],
                leftX, upperY, 0f,
                topColor[0], topColor[1], topColor[2], topColor[3],
                rightX, upperY, 0f,
                topColor[0], topColor[1], topColor[2], topColor[3],
                rightX, lowerY, 0f,
                bottomColor[0], bottomColor[1], bottomColor[2], bottomColor[3]
        };

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }
    /**
     * The create method will initialize everything for openGl.  It must be called after
     * openGL has been initialized system wide, and before calling the draw method.
     */
    public void create() {
        allocateBuffers();
        createProgram();
        createMatrices();
        // Last we check for an error
        // if the error code is not 0, throw an exception.
        OpenGLUtil.checkGlError(TAG, "Create");
    }

    private void allocateBuffers() {
        // initialize vertex byte buffer for shape coordinates
        // The size of the byte buffer is the number of coordinate
        // values times the number of bytes per coordinate (4 for float)
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                coords.length * 4);
        // The order of the bytes should be in the same order as the device
        // hardware's native byte order
        byteBuffer.order(ByteOrder.nativeOrder());

        // Setup the Vertex buffer as a floating point buffer, then add the coordinates
        // into the buffer.  Laset, set the position of the buffer to 0.
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

        // Create the draw list buffer.  The draw list buffer specifies
        // the order to draw the indices.
        ByteBuffer dlb = ByteBuffer.allocateDirect(INDICES_ORDER.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(INDICES_ORDER);
        drawListBuffer.position(0);
    }

    private void createProgram() {
        // Load the shader code into OpenGL
        int vertexShaderHandle =
                OpenGLUtil.createShader(GLES20.GL_VERTEX_SHADER, STATIC_VERTEX_SHADER_CODE);
        int fragmentShaderHandle =
                OpenGLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, STATIC_FRAGMENT_SHADER_CODE);

        // Create a program object and store the handle to it.
        programId = GLES20.glCreateProgram();

        if (programId != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programId, vertexShaderHandle);
            GLES20.glAttachShader(programId, fragmentShaderHandle);


            // Bind the fragment shader to the program.
            // Bind attribute which were declared in the shader code.
            GLES20.glBindAttribLocation(programId, 0, "a_Position");
            GLES20.glBindAttribLocation(programId, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programId);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programId);
                programId = 0;
                throw new OpenGLException("Failed to create rectangle program.");
            }
        }
    }

    public void createMatrices() {
        // Clear our matrices
        for(int i=0;i<16;i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, viewWidth, 0.0f, viewHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
    }

    /**
     * Draws the object to the surface with OpenGL
     */
    public void draw() {
        OpenGLUtil.checkGlError(TAG, "draw");
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(programId);
        OpenGLUtil.checkGlError(TAG, "programId");

        // get handle to vertex shader's a_position member
        int positionHandle = GLES20.glGetAttribLocation(programId, "a_Position");
        OpenGLUtil.checkGlError(TAG, "a_Position");

        GLES20.glEnableVertexAttribArray(positionHandle);

        // Pass in the position information
        vertexBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, NUMBER_OF_COORDS_IN_POSITION,
                GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);

        OpenGLUtil.checkGlError(TAG, "positionHandle");

        int colorHandle = GLES20.glGetAttribLocation(programId, "a_Color");
        OpenGLUtil.checkGlError(TAG, "a_Color");
        GLES20.glEnableVertexAttribArray(colorHandle);

        // Pass in the color information
        vertexBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, NUMBER_OF_COORDS_IN_COLOR, GLES20.GL_FLOAT, false,
                STRIDE_BYTES, vertexBuffer);

        OpenGLUtil.checkGlError(TAG, "ColorHandle");

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0);

        // Draw the rectangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES_ORDER.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
