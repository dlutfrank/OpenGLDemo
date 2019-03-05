package com.swx.opengldemo.shape;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by swx on 2019/3/5.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class Square extends BaseShape {

    private ShortBuffer drawListBuffer;

    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right

    static float color[] = { 0.0f, 0.0f, 1.0f, 1.0f };

    static short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Square(float[] coords, float[] color) {
        super(coords, color);
    }

    public Square(){
        this(squareCoords, color);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        this.prepareShader(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void draw() {
        GLES20.glUseProgram(mProgram);

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandler, coordsPerVertex,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandler, 1, color, 0);
        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0, vertexCount);

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
