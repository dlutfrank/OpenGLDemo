package com.swx.opengldemo.view;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import com.swx.opengldemo.render.ShapeRender;

/**
 * Created by swx on 2019/3/7.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class OnTouchProxy implements View.OnTouchListener {
    private final float TOUCH_SCALE_FACTOR = 180 / 320.0f;
    private float previousX;
    private float previousY;

    private ShapeRender render = null;

    public void setRender(ShapeRender render){
        this.render = render;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float dx;
        float dy;
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                previousX = x;
                previousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                dx = x - previousX;
                dy = y - previousY;

                if(y > view.getHeight() / 2){
                    dx = dx * -1;
                }

                if(x < view.getWidth() / 2){
                    dy = dy * -1;
                }
                if(render!= null){
                    render.setAngle(render.getAngle() + (dx + dy) * TOUCH_SCALE_FACTOR);
                    if(view instanceof GLSurfaceView) {
                        ((GLSurfaceView)view).requestRender();
                    }
                }
                break;
                default:
                    // do noting
        }
        previousY = y;
        previousX = x;
        return true;
    }
}
