package com.hong.utils;

import java.net.URL;

public class PathUtil {

    public static final String PATH;

    static {
        ClassLoader classLoader = PathUtil.class.getClassLoader();
        URL url = classLoader.getResource("file");
        String add = url.toString();
        add = add.replaceAll("file:/", "");
        add = add.replaceAll("/", "\\\\");
        PATH = add;
    }

    public static String getTxt(String fileName) {
        System.out.println(fileName);
        return PATH + "\\" + fileName;
    }
}
