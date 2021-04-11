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
				"([^:]+:\\{(.*)\\})?"+
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
					map = parsetJson( mat.group(3) );   //瑙ｆ瀽缁� (3), 鍙樻垚 [閿�煎]銆�
					command.data = map;
				break;
				case "getList":
				break;
			}
		}
		return command;
	}
	
	//[2] 缁欏鎴风鐢ㄧ殑銆�
	public static Command parseServerCommand(String line){
		String regex = "\\{"+
				"op:([^,]+),?"+
				"(result:([^,]+),?)?"+
				"([^:]+:\\{(.*)\\})?"+
				"(list:\\[([^]]+)\\])?"+
				"\\}";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(line);
		Map<String,String> map = null;
		Map<String,User> users = null;
		Command command = null;
		if( mat.matches() ){
			String op = mat.group(1);
			command = new Command( op, mat.group(3) );  //缁� (3): "yes" / "no"
			System.out.println( "[鏈嶅姟鍣ㄧ] OP:"+ command.op );
			System.out.println( "[鏈嶅姟鍣ㄧ] result:"+ command.result );
			switch( op ){
				case "register":
				case "login":
				case "message":
					map = parsetJson( mat.group(5) );   //瑙ｆ瀽缁� (5), 鍙樻垚 [ 閿�煎 ]銆�
					command.data = map;
					System.out.println( command.data );
				break;
				case "getList":
					users = splitList( mat.group(7) );
					command.users = users;
				break;
			}
		}
		return command;
	}

	private static Map<String,User> splitList(String line){
		String regex = "\\{([^}]+)\\},?";
		Pattern pat = Pattern.compile( regex );
		Matcher mat = pat.matcher( line );
		Map<String,User> users = new LinkedHashMap<String,User>();   //淇濆瓨鏄涓� User
		Map<String,String> map = null;
		User user = null;
		while( mat.find() ){
			map = parsetJson( mat.group(1) );        //涓�涓� User 閲岄潰鐨勫睘鎬�
			user = makeUser( map );
			users.put( user.getSocketId(), user );   //瀛樺叆   Map 褰撲腑
		}
		return users;   //杩斿洖鍝竴涓� Map
	}
	
	public static User makeUser(Map<String,String> data){
		User user = new User();
		//[Note] 缁欐瘡涓�涓垚鍛樺彉閲忚祴鍊� -------------------------
		user.setName( data.get("name") );
		user.setPass( data.get("pass") );
		user.setNickname( data.get("nickname") );
		user.setMark( data.get("mark") );
		user.setSocketId( data.get("socketId") );
		user.setImg( data.get("img") );
		return user;
	}

	public static Map<String,String> parsetJson(String line){
		Map<String,String> data = new HashMap<String,String>();  // user:andy,pass:123
		String regex = "([^:}{]+):([^,]+),?";
		Pattern pat = Pattern.compile(regex);    //缂栬瘧姝ｅ垯琛ㄨ揪寮�  鐢熸垚----> 姝ｅ垯琛ㄨ揪寮忓璞� 
		Matcher mat = pat.matcher( line );       //姝ｅ垯琛ㄨ揪寮忓璞�  寰楀埌鍖归厤鍣�
		while( mat.find() ){
			data.put(mat.group(1), mat.group(2));
		}
		return data;
	}
	
	public static User getUserByLine(String line){
		Map<String,String> data = parsetJson( line );
		return makeUser( data );
	}
	
	public static String makeUserList(Map<String,User> users){
		StringBuffer sb = new StringBuffer("{op:getList,list:[");
		for( String key : users.keySet() ){
			User user = users.get( key );
			sb.append( "{"+ user.toString() +"}," );
		}
		if( users.size() > 0 ){
			sb.setLength( sb.length()-1 );
		}
		sb.append("]}");
		return sb.toString();
	}

}

//[1] 娴嬭瘯娉ㄥ唽鐨勬儏鍐点��
//String line = "{"+
//  "op:register,"+
//  "user:{name:andy,pass:123,nickName:椋庢竻鎵�,mark:浜哄墤鍚堜竴,img:1}"+
//  "}";
//[2] 娴嬭瘯鐧婚檰鐨勬儏鍐点��		
//String line = "{op:login,user:{name:andy,pass:123}}";
//[3] 鑾峰彇鐢ㄦ埛鍒楄〃銆�	
//String line = "{op:getList}";
//[4] 鍙戦�佹秷鎭��
//String line = "{op:send,content:{msg:浣犲ソ鍚�,target:socket001}}";
//parseClientCommand( line );

//---------------------------------------------------------------------------------------

//[1] 娉ㄥ唽鎴愬姛 [ 瀹㈡埛绔帴鏀� ]
//String line = "{"+
//	"op:register,result:yes,"+ 
//	"user:{name:andy,pass:123,nickName:鐗涚帇,mark:鐗涗笉鎬曡檸,img:1}"+
//	"}";

//[2] 娉ㄥ唽澶辫触  [ 瀹㈡埛绔帴鏀� ]
//String line = "{"+
//	"op:register,result:no,"+ 
//	"user:{}"+
//	"}";

//[3] 鐧婚檰鎴愬姛  [ 瀹㈡埛绔帴鏀� ]
//String line = "{"+
//	"op:login,result:yes,"+
//	"user:{user:andy, nickName:鐗涘摜, mark:鐗涚妸涓嶆�曡檸,img:1,socketId:xxx}"+
//	"}";

//[4] 鐧婚檰澶辫触  [ 瀹㈡埛绔帴鏀� ] ( 鐪佺暐 )



