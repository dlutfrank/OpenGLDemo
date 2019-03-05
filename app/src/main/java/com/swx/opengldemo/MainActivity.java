package com.swx.opengldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.swx.opengldemo.databinding.ActivityMainBinding;
import com.swx.opengldemo.render.ShapeRender;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main );
        glSurfaceView = mBinding.glView;
        GLSurfaceView.Renderer render = new ShapeRender();
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(render);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
