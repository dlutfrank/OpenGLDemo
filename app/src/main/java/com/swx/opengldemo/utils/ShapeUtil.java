package com.swx.opengldemo.utils;

import java.security.InvalidParameterException;

/**
 * Created by swx on 2019/3/12.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class ShapeUtil {
    public static float[] createShape(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        final int count = n + 1;
        float[] result = new float[count*3];
        int i = 0;
        for(i = 0; i< 3; i++){
            result[i] = 0.0f;
        }
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * 3; i+=3){
            result[i] = (float) (Math.sin(arcs));
            result[i+1] = (float)(Math.cos(arcs));
            result[i+2] = h;
            arcs += arc;
        }
        return result;

    }
}
