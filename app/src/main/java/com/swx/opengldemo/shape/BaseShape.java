package com.swx.opengldemo.shape;

import android.opengl.GLES20;

import com.swx.opengldemo.render.ShapeRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by swx on 2019/3/5.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public abstract class BaseShape implements Drawable{

    private static final int COORDS_PER_VERTEX = 3;

    protected FloatBuffer vertexBuffer;
    protected final int mProgram;

    protected int positionHandler;
    protected int colorHandler;

    protected final float[] coords;
    protected final float[] color;

    protected final String vertexShaderCoder;
    protected final String fragmentShaderCoder;

    // 每个顶点的坐标个数

    protected final int coordsPerVertex;
    // 顶点数

    protected final int vertexCount;
    // 每个顶点需要占据的空间

    protected final int vertexStride;

    BaseShape(float[] coords, float[] color, String vertexShader, String fragmentShader) {
        this(coords, color, COORDS_PER_VERTEX, vertexShader, fragmentShader);
    }


    BaseShape(float[] coords, float[] color, int coordsPerVertex, String vertexShader, String fragmentShader) {
        this.coords = coords;
        this.color = color;
        this.vertexShaderCoder = vertexShader;
        this.fragmentShaderCoder = fragmentShader;
        this.coordsPerVertex = coordsPerVertex;
        this.vertexCount = this.coords.length / this.coordsPerVertex;
        this.vertexStride = this.coordsPerVertex * 4;
        mProgram = GLES20.glCreateProgram();

        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);
        this.prepareShader(vertexShaderCoder, fragmentShaderCoder);
    }

    private void prepareShader(String vertexShaderCoder, String fragmentShaderCoder) {
        int vertexShader = ShapeRender.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCoder);
        int fragmentShader = ShapeRender.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCoder);
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }
}
