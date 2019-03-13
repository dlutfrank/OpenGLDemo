package com.swx.opengldemo.shape;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Created by swx on 2019/3/4.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class Triangle extends BaseShape {

    static float defaultCoords[] = {
            0.0f, 0.618f, 0.0f,
            -0.382f, -0.382f, 0.0f,
            0.382f, -0.382f, 0.0f,
    };

//    static float color[] = { 0.0f, 1.0f, 0.0f, 1.0f};

    static float[] defaultColor =
                   {0.0f, 1.0f, 0.0f, 1.0f,
                    1.0f,0.0f,0.0f,1.0f,
                    0.0f,0.0f,1.0f,1.0f};


    private int vpMatrixHandle;
    private FloatBuffer colorBuffer;

// attribute一般用于每个顶点都各不相同的量。
// uniform一般用于对同一组顶点组成的3D物体中各个顶点都相同的量。
// varying一般用于从顶点着色器传入到片元着色器的量。

    // 这里是通过顶点着色器修改的片元着色器颜色，也可以直接修改片元着色器颜色

    private static String defaultVertexShaderCoder =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "varying vec4 vColor;"+
            "attribute vec4 aColor;"+
            "void main() {" +
            "gl_Position = uMVPMatrix * vPosition;" +
            "vColor=aColor;"+
            "}";
    private static String defaultFragmentShaderCoder =
            "precision mediump float;" +
            "varying vec4 vColor;" +
            "void main() {" +
            " gl_FragColor = vColor;" +
            "}";

    public Triangle() {
        this(defaultCoords, defaultColor, defaultVertexShaderCoder, defaultFragmentShaderCoder);
    }

    public Triangle(float[] coords, float[] color, String vertexShader, String fragmentShader) {
        super(coords, color, vertexShader, fragmentShader);
        ByteBuffer bb = ByteBuffer.allocateDirect(color.length * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

    @Override
    public void draw(float[] vpMatrix) {
        GLES20.glUseProgram(mProgram);

        if(vpMatrix != null && vpMatrix.length > 0){
            vpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(vpMatrixHandle,1,false,vpMatrix,0);
        }

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, coordsPerVertex,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

//        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
//        GLES20.glUniform4fv(colorHandler, 1, color, 0);

        colorHandler = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandler);
        GLES20.glVertexAttribPointer(colorHandler,4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        // get handle to fragment shader's vColor member
//        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
//        GLES20.glUniform4fv(colorHandler, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
