package com.gec.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gec.model.User;

public class CmdParser {

    public static Command parseClientCommand(String line) {
        String regex = "\\{" +
                "op:([^,]+),?" +
                "([^:]+:\\{(.*)\\})?" +
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
                    map = parsetJson(mat.group(3));   //������ (3), ��� [��ֵ��]��
                    command.data = map;
                    break;
                case "getList":
                    break;
            }
        }
        return command;
    }

    //[2] ���ͻ����õġ�
    public static Command parseServerCommand(String line) {
        String regex = "\\{" +
                "op:([^,]+),?" +
                "(result:([^,]+),?)?" +
                "([^:]+:\\{(.*)\\})?" +
                "(list:\\[([^]]+)\\])?" +
                "\\}";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        Map<String, String> map = null;
        Map<String, User> users = null;
        Command command = null;
        if (mat.matches()) {
            String op = mat.group(1);
            command = new Command(op, mat.group(3));  //�� (3): "yes" / "no"
            System.out.println("[��������] OP:" + command.op);
            System.out.println("[��������] result:" + command.result);
            switch (op) {
                case "register":
                case "login":
                case "message":
                    map = parsetJson(mat.group(5));   //������ (5), ��� [ ��ֵ�� ]��
                    command.data = map;
                    System.out.println(command.data);
                    break;
                case "getList":
                    users = splitList(mat.group(7));
                    command.users = users;
                    break;
            }
        }
        return command;
    }

    private static Map<String, User> splitList(String line) {
        String regex = "\\{([^}]+)\\},?";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(line);
        Map<String, User> users = new LinkedHashMap<String, User>();   //�����Ƕ�� User
        Map<String, String> map = null;
        User user = null;
        while (mat.find()) {
            map = parsetJson(mat.group(1));        //һ�� User ���������
            user = makeUser(map);
            users.put(user.getSocketId(), user);   //����   Map ����
        }
        return users;   //������һ�� Map
    }

    public static User makeUser(Map<String, String> data) {
        User user = new User();
        //[Note] ��ÿһ����Ա������ֵ -------------------------
        user.setName(data.get("name"));
        user.setPass(data.get("pass"));
        user.setNickName(data.get("nickName"));
        user.setMark(data.get("mark"));
        user.setSocketId(data.get("socketId"));
        user.setImg(data.get("img"));
        return user;
    }

    public static Map<String, String> parsetJson(String line) {
        Map<String, String> data = new HashMap<String, String>();  // user:andy,pass:123
        String regex = "([^:}{]+):([^,]+),?";
        Pattern pat = Pattern.compile(regex);    //����������ʽ  ����----> ������ʽ����
        Matcher mat = pat.matcher(line);       //������ʽ����  �õ�ƥ����
        while (mat.find()) {
            data.put(mat.group(1), mat.group(2));
        }
        return data;
    }

    public static User getUserByLine(String line) {
        Map<String, String> data = parsetJson(line);
        return makeUser(data);
    }

    public static String makeUserList(Map<String, User> users) {
        StringBuffer sb = new StringBuffer("{op:getList,list:[");
        for (String key : users.keySet()) {
            User user = users.get(key);
            sb.append("{" + user.toString() + "},");
        }
        if (users.size() > 0) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("]}");
        return sb.toString();
    }

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

//---------------------------------------------------------------------------------------

//[1] ע��ɹ� [ �ͻ��˽��� ]
//String line = "{"+
//	"op:register,result:yes,"+ 
//	"user:{name:andy,pass:123,nickName:ţ��,mark:ţ���»�,img:1}"+
//	"}";

//[2] ע��ʧ��  [ �ͻ��˽��� ]
//String line = "{"+
//	"op:register,result:no,"+ 
//	"user:{}"+
//	"}";

//[3] ��½�ɹ�  [ �ͻ��˽��� ]
//String line = "{"+
//	"op:login,result:yes,"+
//	"user:{user:andy, nickName:ţ��, mark:ţ�����»�,img:1,socketId:xxx}"+
//	"}";

//[4] ��½ʧ��  [ �ͻ��˽��� ] ( ʡ�� )



