package com.swx.opengldemo.shape;

/**
 * Created by swx on 2019/3/13.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */

public class Cone extends BaseShape {
    public Cone(float[] coords, float[] color, String vertexShader, String fragmentShader) {
        super(coords, color, vertexShader, fragmentShader);
    }

    @Override
    public void draw(float[] vpMatrix) {

    }
}
