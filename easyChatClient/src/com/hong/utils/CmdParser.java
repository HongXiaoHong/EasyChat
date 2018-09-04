package com.hong.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hong.model.User;

public class CmdParser {

	public static Command parseClientCommand(String line){
		String regex = "\\{"+
				"op:([^,]+),?"+
				"([^:]+:\\{([^}]*)\\})?"+
				"\\}";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(line);
		Map<String,String> map = null;
		Command command = null;
		if( mat.matches() ){
			String op = mat.group(1);
			command = new Command( op, null );
			switch( op ){
				case "register":
				case "login":
				case "send":
					map = parsetJson( mat.group(3) );   //解析组 (3), 变成 [键值对]。
					command.data = map;
				break;
				case "getList":
				break;
			}
		}
		return command;
	}
	
	//[2] 给客户端用的。
	public static Command parseServerCommand(String line){
		String regex = "\\{"+
				"op:([^,]+),?"+
				"(result:([^,]+),?)?"+
				"([^:]+:\\{([^}]*)\\})?"+
				"(list:\\[([^]]+)\\])?"+
				"\\}";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(line);
		Map<String,String> map = null;
		Command command = null;
		if( mat.matches() ){
			String op = mat.group(1);
			String result = mat.group(3);
			command = new Command( op, result );
			
			System.out.println( "[易聊客户端] 解析类op:"+ command.op );
			switch( op ){
				case "register":
				case "login":
				case "message":
					map = parsetJson( mat.group(5) );   //解析组 (5), 变成 [ 键值对 ]。
					command.data = map;
					System.out.println( command.data );
				break;
				case "getList":
					command.users = splitList( mat.group(7) );
				break;
			}
		}
		return command;
	}
	
	private static Map<String,User> splitList(String line){
		String regex = "\\{([^}]+)\\},?";
		Pattern pat = Pattern.compile( regex );
		Matcher mat = pat.matcher( line );
		Map<String,User> users = new LinkedHashMap<String,User>();
		Map<String,String> map = null;
		User user = null;
		while( mat.find() ){
			map = parsetJson( mat.group(1) );
			user = makeUser( map );
			users.put( user.getSocketId(), user );
		}
		return users;
	}
	
	public static User makeUser(Map<String,String> data){
		User user = null;
		// 新建一个用户
		user = new User();
		user.setName(data.get("name"));
		user.setPass(data.get("pass"));
		user.setNickname(data.get("nickname"));
		user.setMark(data.get("mark"));
		user.setImg(data.get("img"));
		user.setSocketId(data.get("socketId"));
		return user;
	}
	
	private static Map<String,String> parsetJson(String line){
		Map<String,String> data = new HashMap<String,String>();  // user:andy,pass:123
		String regex = "([^:}{]+):([^,]+),?";
		Pattern pat = Pattern.compile(regex);    //编译正则表达式  生成----> 正则表达式对象 
		Matcher mat = pat.matcher( line );       //正则表达式对象  得到匹配器
		while( mat.find() ){
			data.put(mat.group(1), mat.group(2));
		}
		return data;
	}

//	public static void main(String[] args) {
		//[1] 注册成功 [ 客户端接收 ]
//		String line = "{"+
//		"op:register,result:yes,"+ 
//		"user:{name:andy,pass:123,nickName:牛王,mark:牛不怕虎,img:1}"+
//		"}";
		
		//[2] 注册失败  [ 客户端接收 ]
//		String line = "{"+
//			"op:register,result:no,"+ 
//			"user:{}"+
//			"}";
		
		//[3] 登陆成功  [ 客户端接收 ]
//		String line = "{"+
//			"op:login,result:yes,"+
//			"user:{user:andy, nickName:牛哥, mark:牛犊不怕虎,img:1,socketId:xxx}"+
//			"}";
		
		//[4] 登陆失败  [ 客户端接收 ] ( 省略 )
		
		//[5] 用户列表 
//		String line = "{op:getList,"+
//		"list:["+
//		   "{name:andy,nickName:安迪,mark:aaa,img:1,socketId:xx1},"+
//		   "{name:candy,nickName:肯迪,mark:aaa,img:1,socketId:xx2},"+
//		   "{name:ken,nickName:肯,mark:aaa,img:1,socketId:xx2}"+
//		"]}";
//		parseServerCommand( line );
//
//	}
	
	
}

//[1] 测试注册的情况。
//String line = "{"+
//  "op:register,"+
//  "user:{name:andy,pass:123,nickName:风清扬,mark:人剑合一,img:1}"+
//  "}";
//[2] 测试登陆的情况。		
//String line = "{op:login,user:{name:andy,pass:123}}";
//[3] 获取用户列表。	
//String line = "{op:getList}";
//[4] 发送消息。
//String line = "{op:send,content:{msg:你好吗,target:socket001}}";
//parseClientCommand( line );
