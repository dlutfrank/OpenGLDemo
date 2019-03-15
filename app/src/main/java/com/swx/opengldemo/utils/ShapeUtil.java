package com.swx.opengldemo.utils;

import java.security.InvalidParameterException;

/**
 * Created by swx on 2019/3/12.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class ShapeUtil {
    public static final int CPV = 3;
    public static float[] createShape(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        // 中心点 + 最后的重合点
        final int count = n + 2;
        float[] result = new float[count*3];
        int i;
        for(i = 0; i < CPV; i++){
            result[i] = 0.0f;
        }
        result[2] = h;
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * CPV; i+=3){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = h;
            arcs += arc;
        }
        return result;
    }

    public static float[] createCone(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        // 中心点 + 最后的重合点
        final int count = n + 2;
        float[] result = new float[count*3];
        int i;
        for(i = 0; i < CPV; i++){
            result[i] = 0.0f;
        }
        result[2] = h;
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * CPV; i+=3){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = 0.0f;
            arcs += arc;
        }
        return result;
    }

    public static float[] createCylinder(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        // 额外增加 最后的重合点
        final int count = 2*n + 2;
        float[] result = new float[count*3];
        int i = 0;
        float arc = (float)(2.0*Math.PI /n);
        float arcs = 0.0f;
        for(; i < count * CPV; i += 6){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = h;

            result[i+3] = result[i];
            result[i+4] = result[i+1];
            result[i+5] = 0.0f;
            arcs += arc;
        }
        return result;
    }

    public static float[] createBall(float radius, int n) {
        if( n <= 2 || (n % 2) != 0){
            throw new InvalidParameterException("n is invalid");
        }
        final int count = n * (n+1);
        float[] result = new float[count * CPV];
        float arc = (float)(2.0*Math.PI /n);
        float longitude = 0.0f;
        float latitude = 0.0f;
        final int half = n/2;
        final int vertexPerLongitude = 2 * (n + 1) * CPV;
        for(int i = 0; i < half; i++ ){
            latitude = 0.0f;
            int index;
            int j;
            for( j=0;j < 2*n*CPV; j+=6 ){
                index = i * vertexPerLongitude + j;
                result[index] = (float)(radius * Math.sin(longitude) * Math.sin(latitude));
                result[index + 1]  = (float)(radius * Math.sin(longitude) * Math.cos(latitude));
                result[index + 2]  = (float)(radius * Math.cos(longitude));

                result[index + 3] = (float)(radius * Math.sin(longitude+arc) * Math.sin(latitude));
                result[index + 4]  = (float)(radius * Math.sin(longitude+arc) * Math.cos(latitude));
                result[index + 5]  = (float)(radius * Math.cos(longitude+arc));
                latitude += arc;
            }
            // 最后一个闭合节点
            index = i * vertexPerLongitude + j;
            result[index] = (float)(radius * Math.sin(longitude) * Math.sin(0));
            result[index + 1]  = (float)(radius * Math.sin(longitude) * Math.cos(0));
            result[index + 2]  = (float)(radius * Math.cos(longitude));

            result[index + 3] = (float)(radius * Math.sin(longitude+arc) * Math.sin(0));
            result[index + 4]  = (float)(radius * Math.sin(longitude+arc) * Math.cos(0));
            result[index + 5]  = (float)(radius * Math.cos(longitude+arc));

            longitude += arc;
        }
        return result;
    }
}
