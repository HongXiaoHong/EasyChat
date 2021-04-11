package com.hong.utils;

import com.hong.model.User;

import java.util.Map;

import static com.hong.contants.Result.YES;

public class Command {

    public String op;
    public String result;
    public String line;
    public Map<String, String> data;
    public Map<String, User> users;

    public Command(String op, String result) {
        this.op = op;
        this.result = result;
    }

    public static Command makeUserReply(String op, String result, User user) {
        Command com = new Command(op, result);
        if (YES.equals(result)) {
            com.line = "{op:" + op + ",result:" + result + ",user:{name:" + user.getName() +
                    "pass:" + user.getPass() + "nickname" + user.getNickname() +
                    "mark:" + user.getMark() + "img:" + user.getImg() + "}}";
            //老师的简洁写法
			/*
			 String _userStr = "";
			 if("yes".equals(result)){
			 _userStr = user.toString();
			 }
			 com.line = String.format("op:%s,result:%s,user:{%s}",
			 op,result,_userStr);
			 */
        } else {
            com.line = "{op:" + op + ",result:" + result + ",user:{}}";
        }
        return com;
    }
}
