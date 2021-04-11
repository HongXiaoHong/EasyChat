package com.hong.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hong.model.User;
import com.hong.utils.PathUtil;

public class UserDao {

	
	private static String path = PathUtil.getTxt("userInfo.txt");
//	private static String path = "F:\\login\\userInfo.txt";
	
	private Map<String, User> users= new HashMap<String, User>();
	public User getUser(User user, int flag){
		
		users = parseJson();
		return users.get(user.getName());
		
	}
	
	
	
	public boolean regist(User user) {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try {
/*			writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(path,true),"GBK"));*/
			writer = new BufferedWriter(
					new FileWriter(path,true));
			
			StringBuffer sb = new StringBuffer();
			sb.append("name:");
			sb.append(user.getName());
			sb.append(",pass:");
			sb.append(user.getPass());
			sb.append(",nickname:");
			sb.append(user.getNickname());
			sb.append(",mark:");
			sb.append(user.getMark());
			sb.append(",img:");
			sb.append(user.getImg());
			
			writer.write( sb.toString() );
			writer.newLine();
			writer.flush();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@SuppressWarnings("resource")
	private static Map<String,User> parseJson(){
		BufferedReader reader = null;
		Map<String, User> map = new HashMap<String, User>();
		
			//
			try {
				reader = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(path),"GBK"));
				String row = null;
				String reg = "([^:]+):([^,]+),?";
				Pattern pattern = Pattern.compile(reg);
				Matcher mat = null;
				String[] personInfo = new String[5];
				int index = 0;
				System.out.println("从文件中读取数据到内存中");
				while ((row=reader.readLine())!=null){
					if(row.length()>0){
						index = 0;
						mat = pattern.matcher(row);
						while(mat.find())
							personInfo[index++] = mat.group(2);
						
						map.put(personInfo[0], 
								new User(personInfo[0],personInfo[1]
										,personInfo[2],personInfo[3],personInfo[4]));
					}
				}
				return map;
			} catch (FileNotFoundException e1) {
				System.out.println("该路径："+path+"，可能由于路径不存在，找不到文件，请检查路径是否正确");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	return null;
	}
		
	
}
