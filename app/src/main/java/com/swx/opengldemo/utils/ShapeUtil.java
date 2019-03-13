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
        // 中心点 + 最后的重合点
        final int count = n + 2;
        float[] result = new float[count*3];
        int i = 0;
        for(i = 0; i< 3; i++){
            result[i] = h;
        }
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * 3; i+=3){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = h;
            arcs += arc;
        }
        return result;
    }
}
