package com.gec.view;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.ImageIcon;

public class ImageLoader {
	public static String PATH;
	static {
		//[1] 获取 images 所在目录的物理路径。
		ClassLoader loader = ImageLoader.class.getClassLoader();
		URL url = loader.getResource("images");
		String addr = url.toString();
		try {
			addr = URLDecoder.decode( addr, "UTF-8" );   //[PS] 对路径进行解码 ..
			addr = addr.replaceAll( "file:/", "" );
			addr = addr.replace("/", "\\");
			PATH = addr;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	//[1] 创建 ImageIcon 图标
	public static ImageIcon getImageIcon(String imgName){
		return new ImageIcon( PATH +"\\"+ imgName );
	}
}

