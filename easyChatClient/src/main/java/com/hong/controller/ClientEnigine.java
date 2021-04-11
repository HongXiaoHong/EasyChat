package com.hong.controller;

import java.util.Map;

import javax.swing.ImageIcon;

import com.hong.model.User;
import com.hong.utils.CmdParser;
import com.hong.utils.Command;
import com.hong.utils.MsgCallBack;
import com.hong.utils.NetWork;
import com.hong.view.ListFrame;

public class ClientEnigine implements MsgCallBack, EngineCallBack{

	NetWork netWork = new NetWork(this);
	String socketId;
	Map<String, User> users ;
	ListFrame listFrame = null;
	
	//新加的
	//---------------------------------------------
	public ListFrame getListFrame() {
		return listFrame;
	}
	public void setListFrame(ListFrame listFrame) {
		this.listFrame = listFrame;
	}
	//---------------------------------------------
	public void startGame(){
		socketId = netWork.startConnect("127.0.0.1", 1090);//localhost,192.168.13.31
		
		
		//Scanner sc = new Scanner(System.in);
/*			User user = new User();
			user.setName("洪晓鸿");
			user.setPass("123456");
			user.setNickname("ping");;
			user.setMark("beautiful ping");;
			user.setImg("8");
			user.setSocketId("1314");
			doLogin(user);*/
		
		/*Scanner sc = new Scanner(System.in);
		while(true){
			prt("请输入要发送的消息");
			String msg = sc.next();
			netWork.send(socketId, msg);
		}*/
/*			while(true){
				String line = sc.next();
				String[] b = line.split(",");
				//socketId,消息
				doSend(b[0],b[1]);
			}*/
			
	}
	public void connect(String ip, int port){
		//连接服务器
		if(socketId==null)
		socketId = netWork.startConnect(ip, port);//localhost,192.168.13.31
	}
	
	public void doLogin(User user) {
		// TODO Auto-generated method stub
		String cmd = String.format("{op:login,user:{name:%s,pass:%s}}", 
				user.getName(),user.getPass());
		netWork.send(socketId, cmd);
	}
	
	public void doRegist(User user) {
		// TODO Auto-generated method stub
		String cmd = String.format("{op:register,"
				+ "user:{name:%s,pass:%s,nickname:%s,mark:%s,img:%s,socketId:%s}}", 
				user.getName(),user.getPass(),user.getNickname(),user.getMark(),user.getImg(),user.getSocketId());
		netWork.send(socketId, cmd);
	}
	public void doSend(String target, String message) {
		// TODO Auto-generated method stub
		String line = String.format("{op:send,content:{msg:%s,target:%s}}", message,target);
		netWork.send(socketId, line);
	}

	@Override
	public void OnConntextEvent(boolean flag) {
		// TODO Auto-generated method stub
		String retStr = flag?"成功":"失败";
		prt("客户端连接服务器    ："+retStr);
	}

	@Override
	public void onReceived(byte[] buff, int count, String socketId) {
		// TODO Auto-generated method stub
		String line = new String(buff,0,count);
		prt("收到消息："+line);
		Command command = CmdParser.parseServerCommand(line);
		String op = command.op;
		String selfId = null;
		
		switch(op){
		case "login":
			
			prt("服务器返回信息login："+command.result);
			if("yes".equals(command.result)){
				//获取服务器返回的socketId
				selfId = command.data.get("socketId");
				System.out.println("客户端收到服务器："+selfId);
				//将新的socketId放入netWork的map集合中
				netWork.copySocket(this.socketId, selfId);
				//更新当前的socketId
				this.socketId = selfId;
				//使用解析器工具类进行解析返回一个user对象
				User user = CmdParser.makeUser(command.data);
				//System.out.println("113 = "+command.data.get("nickname"));
				//---------------------------------------------
				//……………………………………………………待定…………………………………………………………
				//---------------------------------------------
				//为什么可以使用this
				listFrame = new ListFrame(this, user);
			}
			break;
		case "getList"://获取得到列表
			System.out.println("获取在线列表");
			users = command.users;
			//从map集合中移除自己
			users.remove(this.socketId);
			if(listFrame!=null)
			listFrame.showList(users);
			prtUsers();
			break;
		case "register":
			
			prt("服务器返回信息register："+command.result);
			break;
		case "message"://接收到客户端发来的信息
			prt("收到消息："+command.data);
			String msg = command.data.get("msg");
			String from = command.data.get("from");
			prt("收到【"+from+"】的消息【"+msg+"】");
			//要设置为来的socketId而不是自己的socketId
			listFrame.showMessage(from, msg);
			break;
		}
	}

	private void prtUsers() {
		// 打印所有用户
		int id = 0;
		System.out.println("users--------value------"+users);
		for(String socketId : users.keySet()){
			id++;
			User user = users.get(socketId);
			System.out.printf("[%d][%s][%s]\n",id,socketId,user.getNickname());
		}
		System.out.println("用户列表已经更新，可以选人发送消息");
	}

	@Override
	public void onReveiveFailed() {
		System.out.println("接受失败");
	}

	public void prt(Object obj){
		System.out.println("【易聊客户端】"+obj);
	}
	
	@Override
	@Deprecated
	public void onCreateServer(boolean flag) {}
	@Override
	@Deprecated
	public void onAccepted() {}

	@Override
	public void onReveived(byte[] buff) {
		// TODO Auto-generated method stub
		String line = new String(buff);
		String[] buffer = line.split(",");
		netWork.send(buffer[0], buffer[1]);
	}
	@Override
	public void doRegister(User user) {
		// ---------------------待定-------------------
		
	}
	@Override
	public User getUser(String socketId) {
		// ------------------------返回socketId对应的用户----------------
		return users.get(socketId);
	}
	@Override
	public ImageIcon getUserHead(String socketId) {
		// ---------------------待定--------------------
		System.out.println("客户端引擎socketId"+socketId);
		User user = users.get(socketId);
		System.out.println("客户端引擎socketId"+user);
		if(user!=null){
			return user.getHead();
		}
		System.out.println("客户端------拿不到ImageIcon");
		return null;
	}

}
