package com.swx.opengldemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.swx.opengldemo.shape.Square;
import com.swx.opengldemo.shape.Triangle;
import com.swx.opengldemo.utils.ResourceUtil;

import java.lang.ref.WeakReference;

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
    private WeakReference<Context> contextRef = null;
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

    public ShapeRender(Context context){
        if(contextRef != null){
            contextRef.clear();
        }
        contextRef = new WeakReference<>(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
         GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);

        mTriangle = new Triangle();
        if(contextRef == null || contextRef.get() == null){
            mSquare = new Square();
        }else{
            Context context = contextRef.get();
            String vertex = ResourceUtil.loadAssertFile(context,"shader/square.vert");
            String fragment = ResourceUtil.loadAssertFile(context, "shader/square.frag");
            mSquare = new Square(vertex, fragment);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix,0,-ratio,ratio,-1,1,3,7);
//        Matrix.frustumM(projectionMatrix,0,-1,1,-ratio,ratio,3,7);
//        Matrix.frustumM(projectionMatrix,0,-1,1,-1,1,3,7);
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
        mSquare.draw(scratch);
//        mTriangle.draw(scratch);
    }
}
