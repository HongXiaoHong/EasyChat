package com.gec.utils;

import java.util.Map;

import com.gec.model.User;

public class Command {

	public String op;
	public String result;
	public String line;
	
	public Map<String,String> data;
	public Map<String, User> users;
	
	public Command(String op,String result){
		this.op = op;
		this.result = result;
	}

	public static Command makeUserReply(String op, String ret, User _user){
		Command cmd = new Command(op, ret);
		String userStr = "";
		if( ret.equals("yes") ){
			userStr = _user.toString();
		}
		cmd.line = String.format( "{op:%s,result:%s,user:{%s}}", op, ret, userStr );
		return cmd;
	}

}
