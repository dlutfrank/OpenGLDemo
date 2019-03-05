package com.swx.opengldemo.shape;

import android.opengl.GLES20;

import com.swx.opengldemo.render.ShapeRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by swx on 2019/3/4.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class Triangle {
    private FloatBuffer vertexBuffer;

    protected final int mProgram;

    private int positionHandler;
    private int colorHandler;

    private final int vertexCount = coords.length / COORDS_PER_VERTEX;

    // 每个顶点需要占据的空间

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // 每个顶点的坐标个数

    static int COORDS_PER_VERTEX = 3;

    static float coords[] = {
            0.0f, 0.618f, 0.0f,
            -0.382f, -0.382f, 0.0f,
            0.382f, -0.382f, 0.0f,
    };

    private final String vertexShaderCoder =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "gl_Position = vPosition;" +
                    "}";
    private final  String fragmentShaderCoder =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";

    float color[] = { 0.0f, 1.0f, 0.0f, 1.0f};

    public Triangle() {
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

        int vertexShader = ShapeRender.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCoder);
        int fragmentShader = ShapeRender.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCoder);
        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);

        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandler, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandler, 1, color, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0, vertexCount);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandler);
    }

}
