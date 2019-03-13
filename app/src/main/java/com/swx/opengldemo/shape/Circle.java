package com.swx.opengldemo.shape;

import android.opengl.GLES20;

/**
 * Created by swx on 2019/3/13.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class Circle extends BaseShape {
    public Circle(float[] coords, float[] color, String vertexShader, String fragmentShader) {
        super(coords, color, vertexShader, fragmentShader);
    }

    @Override
    public void draw(float[] vpMatrix) {
        GLES20.glUseProgram(mProgram);

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, coordsPerVertex,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandler, 1, color, 0);

        int matrixHandler = GLES20.glGetUniformLocation(mProgram, "vpMatrix");
        GLES20.glUniformMatrix4fv(matrixHandler, 1, false, vpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
