package com.swx.opengldemo.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by swx on 2019/3/11.
 * Mail: dlut_frank@163.com
 * Copyright (c) 2019 .
 */
public class ResourceUtil {
    public static String loadAssertFile(Context context, String path){
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(path);
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while (null != (str = bf.readLine())){
                sb.append(str);
            }
            bf.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return sb.toString();
    }
}
