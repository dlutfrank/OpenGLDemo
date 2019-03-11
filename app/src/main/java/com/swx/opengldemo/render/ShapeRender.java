package com.swx.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.swx.opengldemo.shape.Square;
import com.swx.opengldemo.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by swx on 2019/3/4.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class ShapeRender implements GLSurfaceView.Renderer {

    private Triangle mTriangle;
    private Square mSquare;
    // vpMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vpMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];

    public volatile float mAngle;

    public float getAngle(){
        return mAngle;
    }

    public void setAngle(float angle){
        this.mAngle = angle;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
//        GLES20.glClearColor(1.0f,0.0f,0.0f, 1.0f);
        mTriangle = new Triangle();
        mSquare = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix,0,-ratio,ratio,-1,1,3,7);
        Matrix.setLookAtM(viewMatrix,0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
        Matrix.multiplyMM(vpMatrix,0,projectionMatrix,0,viewMatrix,0);
    }

    public static  int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER) 顶点着色器
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER) 片源着色器
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return  shader;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        // Calculate the projection and view transformation
        float[] scratch = new float[16];
//        long time = SystemClock.uptimeMillis() % 4000;
//        float angle = 0.090f * ((int)time);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0,0, -1.0f);
        Matrix.multiplyMM(scratch,0, vpMatrix, 0, rotationMatrix,0);
//        mSquare.draw();
        mTriangle.draw(scratch);
//        mTriangle.draw();
    }
}
