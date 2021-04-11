package com.hong.utils;

import com.hong.model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdParser {

    public static Command parseClientCommand(String line) {
        String regex = "\\{" +
                "op:([^,]+),?" +
                "([^:]+:\\{([^}]*)\\})?" +
                "\\}";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        Map<String, String> map = null;
        Command command = null;
        if (mat.matches()) {
            String op = mat.group(1);
            command = new Command(op, null);
            switch (op) {
                case "register":
                case "login":
                case "send":
                    map = parsetJson(mat.group(3));
                    command.data = map;
                    break;
                case "getList":
                    break;
                default:
            }
        }
        return command;
    }

    //[2] ���ͻ����õġ�
    public static Command parseServerCommand(String line) {
        String regex = "\\{" +
                "op:([^,]+),?" +
                "(result:([^,]+),?)?" +
                "([^:]+:\\{([^}]*)\\})?" +
                "(list:\\[([^]]+)\\])?" +
                "\\}";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        Map<String, String> map = null;
        Command command = null;
        if (mat.matches()) {
            String op = mat.group(1);
            String result = mat.group(3);
            command = new Command(op, result);

            System.out.println("[���Ŀͻ���] ������op:" + command.op);
            switch (op) {
                case "register":
                case "login":
                case "message":
                    map = parsetJson(mat.group(5));   //������ (5), ��� [ ��ֵ�� ]��
                    command.data = map;
                    System.out.println(command.data);
                    break;
                case "getList":
                    command.users = splitList(mat.group(7));
                    break;
            }
        }
        return command;
    }

    private static Map<String, User> splitList(String line) {
        String regex = "\\{([^}]+)\\},?";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        Map<String, User> users = new LinkedHashMap<String, User>();
        Map<String, String> map = null;
        User user = null;
        while (mat.find()) {
            map = parsetJson(mat.group(1));
            user = makeUser(map);
            users.put(user.getSocketId(), user);
        }
        return users;
    }

    public static User makeUser(Map<String, String> data) {
        User user = null;
        // �½�һ���û�
        user = new User();
        user.setName(data.get("name"));
        user.setPass(data.get("pass"));
        user.setNickname(data.get("nickname"));
        user.setMark(data.get("mark"));
        user.setImg(data.get("img"));
        user.setSocketId(data.get("socketId"));
        return user;
    }

    private static Map<String, String> parsetJson(String line) {
        Map<String, String> data = new HashMap<String, String>();  // user:andy,pass:123
        String regex = "([^:}{]+):([^,]+),?";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        while (mat.find()) {
            data.put(mat.group(1), mat.group(2));
        }
        return data;
    }

//	public static void main(String[] args) {
    //[1] ע��ɹ� [ �ͻ��˽��� ]
//		String line = "{"+
//		"op:register,result:yes,"+ 
//		"user:{name:andy,pass:123,nickName:ţ��,mark:ţ���»�,img:1}"+
//		"}";

    //[2] ע��ʧ��  [ �ͻ��˽��� ]
//		String line = "{"+
//			"op:register,result:no,"+ 
//			"user:{}"+
//			"}";

    //[3] ��½�ɹ�  [ �ͻ��˽��� ]
//		String line = "{"+
//			"op:login,result:yes,"+
//			"user:{user:andy, nickName:ţ��, mark:ţ�����»�,img:1,socketId:xxx}"+
//			"}";

    //[4] ��½ʧ��  [ �ͻ��˽��� ] ( ʡ�� )

    //[5] �û��б�
//		String line = "{op:getList,"+
//		"list:["+
//		   "{name:andy,nickName:����,mark:aaa,img:1,socketId:xx1},"+
//		   "{name:candy,nickName:�ϵ�,mark:aaa,img:1,socketId:xx2},"+
//		   "{name:ken,nickName:��,mark:aaa,img:1,socketId:xx2}"+
//		"]}";
//		parseServerCommand( line );
//
//	}


}

//[1] ����ע��������
//String line = "{"+
//  "op:register,"+
//  "user:{name:andy,pass:123,nickName:������,mark:�˽���һ,img:1}"+
//  "}";
//[2] ���Ե�½�������		
//String line = "{op:login,user:{name:andy,pass:123}}";
//[3] ��ȡ�û��б�	
//String line = "{op:getList}";
//[4] ������Ϣ��
//String line = "{op:send,content:{msg:�����,target:socket001}}";
//parseClientCommand( line );
