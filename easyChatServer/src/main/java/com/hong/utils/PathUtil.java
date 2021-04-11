package com.hong.utils;

import java.net.URL;

public class PathUtil {

	public static final String PATH;
	static{
		ClassLoader classLoader = PathUtil.class.getClassLoader();
		URL url = classLoader.getResource("file");
		String add = url.toString();
		add = add.replaceAll("file:/", "");
		add = add.replaceAll("/", "\\\\");
		PATH = add;
	}
	public static String getTxt(String fileName){
		System.out.println(fileName);
		return PATH+"\\"+fileName;
	}
/*	public static void main(String[] args) {
		System.out.println(getTxt("userInfo.txt"));
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(getTxt("userInfo.txt")));
			String line = null;
			while (((line = reader.readLine())!=null)){
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
