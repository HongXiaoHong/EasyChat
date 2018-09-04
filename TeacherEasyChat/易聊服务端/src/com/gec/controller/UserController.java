package com.gec.controller;

import com.gec.dao.UserDao;
import com.gec.model.User;
import com.gec.utils.Command;

public class UserController {

	UserDao userDao = new UserDao();
	public Command register(User _user){
		//[1] 判断用户名在不在数据库当中出现。
		User daoUser = userDao.getUser(_user, User.REGISTER);
		Command command = null;
		if( daoUser!=null ){   //[2] 用户名存在 , 不可以注册
			System.out.println("[SERVER] 用户名存在 , 不可以注册。");
			command = Command.makeUserReply("register", "no", _user);
		}else{    //[3] 用户名不存在 , 可以注册
			System.out.println("[SERVER] 用户名不存在 , 可以注册。");
			boolean ret = userDao.addUser(_user);
			if( ret ){
				command = Command.makeUserReply("register", "yes", _user);
			}else{
				System.out.println("[UserController] 数据库写入失败。");
			}
		}
		return command;
	}

	public Command login(User _user){
		//[1] 验证  "用户名" + "密码" 在不在数据库当中出现。
		User daoUser = userDao.getUser(_user, User.LOGIN);
		Command command = null;
		if( daoUser!=null ){    //存在, 登陆成功...
			System.out.println("[SERVER] 登陆成功  ...");
			//[2] 填充完整, 形参上  _user 的信息。
			//    _user 原有的信息: name, pass, socketId
			_user.setNickName( daoUser.getNickName() );    // [ 数据库中获得 ]
			_user.setMark( daoUser.getMark() );            // [ 数据库中获得 ]
			_user.setImg( daoUser.getImg() );              // [ 数据库中获得 ]
			
			command = Command.makeUserReply("login", "yes", _user);
		}else{   //不存在用户, 登陆失败 ...
			System.out.println("[SERVER] 登陆失败 ...");
			command = Command.makeUserReply("login", "no", _user);
		}
		return command;
	}
	
}


//UserController con = new UserController();
//User user = new User();
//user.setName( "kk" );
//user.setPass( "234" );
//user.setNickName( "卡卡" );
//user.setMark( "卡西卡西" );
//user.setImg("1");
//con.register( user );



//UserController con = new UserController();
//User user = new User();
//Scanner sc = new Scanner( System.in );
//String line = sc.next();
//String[] arr = line.split(",");
//user.setName( arr[0] );
//user.setPass( arr[1] );
//con.login( user );
//System.out.println( user );





