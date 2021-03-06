package com.hong.utils;

import javax.swing.*;
import java.net.URL;

public class PathUtil {

    public static final String PATH;

    static {
        ClassLoader classLoader = PathUtil.class.getClassLoader();
        URL url = classLoader.getResource("images");
        String add = url.toString();
        add = add.replaceAll("file:/", "");
        add = add.replaceAll("/", "\\\\");
        PATH = add;
    }

    public static ImageIcon getImageIcon(String fileName) {
        System.out.println(fileName);
        return new ImageIcon(PATH + "\\" + fileName);
    }
}
