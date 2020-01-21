package com.luckstro.videooverlay.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by ejdd5cj on 7/1/2017.
 */

public class Triangle {
    private static final String TAG = "Triangle";
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
    private static short[] INDICES_ORDER = new short[] {0, 1, 2};

    /** This shader will set the color and position of the rectangle.  It position
     of the vertexs will not change with this shader. */
    private static final String STATIC_VERTEX_SHADER_CODE =
            "uniform 	mat4 		uMVPMatrix;" +
                    "attribute 	vec4 		vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    /** This fragment shader will pass the color on through to opengl.  The color
     will not change with this shader. */
    private static final String STATIC_FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.5,0,0,1);" +
                    "}";


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

    public Triangle(float viewWidth, float viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public void create() {
        createTriangleCoordinates(viewWidth, viewHeight);
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

        // Load the shader code into OpenGL
        int vertexShaderHandle =
                OpenGLUtil.createShader(GLES20.GL_VERTEX_SHADER, STATIC_VERTEX_SHADER_CODE);
        int fragmentShaderHandle =
                OpenGLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, STATIC_FRAGMENT_SHADER_CODE);

        // Create a program object and store the handle to it.
        programId = GLES20.glCreateProgram();

        if (programId != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programId, vertexShaderHandle);
            GLES20.glAttachShader(programId, fragmentShaderHandle);

            // Bind the fragment shader to the program.
            // Bind attribute which were declared in the shader code.
            GLES20.glBindAttribLocation(programId, 0, "vPosition");

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

        // Last we check for an error
        // TODO move this to the OpenGLUtil and have it return an error code.
        // if the error code is not 0, throw an exception.
        TextureRender.checkGlError("Rectangle create");

        updateSurfaceSize(viewWidth, viewHeight);
    }

    public void updateSurfaceSize(float width, float height) {
        // Clear our matrices
        for(int i=0;i<16;i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, width, 0.0f, height, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
    }

    private void createTriangleCoordinates(float width, float height) {
        float leftX = 0f;
        float rightX = width/2;
        float upperY = height/2;
        float lowerY = 0f;

        coords = new float[]{
                rightX, upperY, 0f,
                leftX, lowerY, 0f,
                leftX, upperY, 0f
        };
    }

    public void draw() {
        // Set to use our shader programm
        GLES20.glUseProgram(programId);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(programId, "vPosition");
        OpenGLUtil.checkGlError(TAG, "draw vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        OpenGLUtil.checkGlError(TAG, "draw enableHandle");

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        OpenGLUtil.checkGlError(TAG, "draw prepareData");

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");
        OpenGLUtil.checkGlError(TAG, "draw getMatrixHandle");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0);
        OpenGLUtil.checkGlError(TAG, "draw applyProjection");

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES_ORDER.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        OpenGLUtil.checkGlError(TAG, "draw drawElements");

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        OpenGLUtil.checkGlError(TAG, "draw disable");
    }
}
