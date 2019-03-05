package com.swx.opengldemo.shape;

import android.opengl.GLES20;

import com.swx.opengldemo.render.ShapeRender;


/**
 * Created by swx on 2019/3/4.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class Triangle extends BaseShape {

    static float coords[] = {
            0.0f, 0.618f, 0.0f,
            -0.382f, -0.382f, 0.0f,
            0.382f, -0.382f, 0.0f,
    };

    static float color[] = { 0.0f, 1.0f, 0.0f, 1.0f};


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

    public Triangle() {
        this(coords, color);
    }

    public Triangle(float[] coords, float[] color) {
        super(coords, color);
        this.prepareShader(vertexShaderCoder, fragmentShaderCoder);
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
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0, vertexCount);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandler);
    }

}
