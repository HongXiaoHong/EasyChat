package com.hong.controller;

import com.hong.dao.UserDao;
import com.hong.model.User;
import com.hong.utils.Command;

public class UserController {

	UserDao dao = new UserDao();

	public Command regist(User user){
		User u = dao.getUser(user, User.REGISTER);
		if (u==null){
			System.out.println("用户名不存在，可以进行注册");
			if(dao.regist(user))
			return Command.makeUserReply("regist", "yes", user);
		}else{
			System.out.println("已经拥有该用户，请勿重复注册！！！");
			return Command.makeUserReply("regist", "no", user);
		}
		return null;
	}
	public Command login(User user){
		User u = dao.getUser(user, User.LOGIN);
		if (u==null){
			System.out.println("没有该用户，请进行注册！！！");
			return Command.makeUserReply("login", "no", user);
		}else{
			if (  u.getName().equals(user.getName()) 
					&& u.getPass().equals(user.getPass())){
				System.out.println("登录成功");
				user.setNickname(u.getNickname());
				user.setMark(u.getMark());
				user.setImg(u.getImg());
				return Command.makeUserReply("login", "yes", user);
			}else {
				System.out.println("登录失败");
				return Command.makeUserReply("login", "no", user);
			}
		}
	}
//	public static void main(String[] args) {
//		UserController con = new UserController();
//		String name = "斯蒂芬";
//		String pass = "123456";
//		User user = new User(name, pass);
///*		user.setNickname("");
//		user.setMark("");
//		user.setImg("3");*/
//		con.login(user);
//		System.out.println(user);
//	}
}
