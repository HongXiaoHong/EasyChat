package com.hong.model;

import java.net.InetSocketAddress;
import java.util.Date;
import java.text.SimpleDateFormat;

public class User {

	public static int LOGIN = 1;
	public static int REGISTER = 2;
	private String name;
	private String pass;
	private String nickName;
	private String mark;
	private String img;
	private String socketId;
	private String ip;
	private int port;
	private String loginTime;
	
	public void setIpAndPort(InetSocketAddress isa){
		ip = isa.getAddress().getHostAddress();
		port = isa.getPort();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		loginTime = format.format(date);
	}
	
	public boolean equals(User user, int flag){
		boolean ret = false;
		if(flag==User.LOGIN){
			if(user.name!=null && user.pass!=null 
					&& name.equals(user.getName()) && pass.equals(user.getPass())){
				ret = true;
			}
		} else if (flag==User.REGISTER){
			if(user.name!=null && name.equals(user.getName())){
				ret = true;
			}
		}else{
			
		}
		return ret;
	}
	
	public User() {
		super();
	}
	
	public User(String name, String pass, String nickName, String mark,
			String img) {
		super();
		this.name = name;
		this.pass = pass;
		this.nickName = nickName;
		this.mark = mark;
		this.img = img;
	}
	
	public User(String name, String pass) {
		// TODO Auto-generated constructor stub
		super();
		this.name = name;
		this.pass = pass;
	}

	public String getNickName() {
		return nickName;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getNickname() {
		return nickName;
	}
	public void setNickname(String nickName) {
		this.nickName = nickName;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getSocketId() {
		return socketId;
	}
	public void setSocketId(String socketId) {
		this.socketId = socketId;
	}
	public String toString(){
		return "name:"+getName()+",pass:"+getPass()+",nickname:"+getNickname()+
				",mark:"+getMark()+",img:"+getImg()+",socketId:"+getSocketId();
	}
}
