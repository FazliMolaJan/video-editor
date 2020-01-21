package com.luckstro.videooverlay.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Created by ejdd5cj on 6/13/2017.
 */

public class RoundedTriangle {
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
    private static short[] INDICES_ORDER = new short[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};

    /** This shader will set the color and position of the rectangle.  It position
     of the vertexs will not change with this shader. */
    private static final String STATIC_VERTEX_SHADER_CODE =
            "attribute vec4 a_Position;     \n"         // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
                    + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                    + "void main() {                  \n"
                    + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                    + "   gl_Position = a_Position;   \n"       // Pass the position through to the fragement shader.
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

    public RoundedTriangle(float leftX, float rightX, float upperY, float lowerY,
                           float[] topColor, float[] bottomColor) {


        coords = createCoordinates(0f, 0f, .15f, 1f, 1f, 1f, 1f);
    }

    private float[] createCoordinates(float x, float y, float radius,
                                      float r, float g, float b, float alpha) {
        int triangleAmount = 20; //# of triangles used to draw circle

        double twicePi = 2.0f * Math.PI;

        ArrayList<Float> coordinatesArray = new ArrayList<>();

        coordinatesArray.add(x);
        coordinatesArray.add(y);
        coordinatesArray.add(0f);
        coordinatesArray.add(r);
        coordinatesArray.add(g);
        coordinatesArray.add(b);
        coordinatesArray.add(alpha);

        float angle = 360/triangleAmount; //degrees?
        for(int i = 0; i <= triangleAmount;i++) {
            float angle2 = angle * (i + 1);
            float anglerad = new Double(angle2 * Math.PI / 180.0f).floatValue();
            float x1 = new Double(Math.sin(anglerad) * radius).floatValue();
            float y2 = new Double(Math.cos(anglerad) * radius).floatValue();
            coordinatesArray.add(x1);
            coordinatesArray.add(y2);
            coordinatesArray.add(0f);
            coordinatesArray.add(r);
            coordinatesArray.add(g);
            coordinatesArray.add(b);
            coordinatesArray.add(alpha);
        }
//        for(int i = 0; i <= triangleAmount;i++) {
//            x = x + new Double(radius * Math.cos(i *  twicePi / triangleAmount)).floatValue();
//            y = y + new Double(radius * Math.sin(i * twicePi / triangleAmount)).floatValue();
//            coordinatesArray.add(x);
//            coordinatesArray.add(y);
//            coordinatesArray.add(0f);
//            coordinatesArray.add(r);
//            coordinatesArray.add(g);
//            coordinatesArray.add(b);
//            coordinatesArray.add(alpha);
//        }
        float[] coordinates = new float[coordinatesArray.size()];
        for (int i=0;i<coordinates.length;i++) {
            coordinates[i] = coordinatesArray.get(i);
        }
        return coordinates;
    }
    /**
     * The create method will initialize everything for openGl.  It must be called after
     * openGL has been initialized system wide, and before calling the draw method.
     */
    public void create() {
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

        programId = programId;

        // Create the draw list buffer.  The draw list buffer specifies
        // the order to draw the indices.
        ByteBuffer dlb = ByteBuffer.allocateDirect(INDICES_ORDER.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(INDICES_ORDER);
        drawListBuffer.position(0);

        // Last we check for an error
        // TODO move this to the OpenGLUtil and have it return an error code.
        // if the error code is not 0, throw an exception.
        TextureRender.checkGlError("RoundedTriangle create");
    }

    /**
     * Draws the object to the surface with OpenGL
     */
    public void draw() {
        TextureRender.checkGlError("RoundedTriangle draw");
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(programId);
        TextureRender.checkGlError("RoundedTriangle programId");

        // get handle to vertex shader's a_position member
        int positionHandle = GLES20.glGetAttribLocation(programId, "a_Position");
        TextureRender.checkGlError("RoundedTriangle a_Position");
        int colorHandle = GLES20.glGetAttribLocation(programId, "a_Color");
        TextureRender.checkGlError("RoundedTriangle a_Color");

        // Pass in the position information
        vertexBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, NUMBER_OF_COORDS_IN_POSITION,
                GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);

        GLES20.glEnableVertexAttribArray(positionHandle);

        TextureRender.checkGlError("RoundedTriangle positionHandle");
        // Pass in the color information
        vertexBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, NUMBER_OF_COORDS_IN_COLOR, GLES20.GL_FLOAT, false,
                STRIDE_BYTES, vertexBuffer);

        GLES20.glEnableVertexAttribArray(colorHandle);
        TextureRender.checkGlError("RoundedTriangle ColorHandle");

        // Draw the rectangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, INDICES_ORDER.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
