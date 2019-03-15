package com.swx.opengldemo.utils;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by swx on 2019/3/15.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class ShapeUtilTest {
    @Test
    public void createBallTest(){
        float[] result = ShapeUtil.createBall(1.0f, 4);
        assert result != null;
        System.out.println(Arrays.toString(result));
    }
}
