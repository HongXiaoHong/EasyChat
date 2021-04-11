package com.gec.controller;

import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;

import com.gec.model.User;
import com.gec.utils.CmdParser;
import com.gec.utils.Command;
import com.gec.utils.MsgCallBack;
import com.gec.utils.NetWork;
import com.gec.view.ListFrame;
import com.gec.view.LoginFrame;

//[1] 实现这一个接口。

public class ClientEngine 
	implements MsgCallBack, EngineCallBack {

	NetWork work = new NetWork(this);
	String socketId;           //这里客户端本方保存的 SocketId
	Map<String,User> users;    //保存在线的用户信息 ..
	
	LoginFrame loginFrame;
	ListFrame listFrame;
	
	public void startGame(){
		loginFrame = new LoginFrame( this );
	}
	
	//[1] EngineCallBack 回调方法   -----------------------------------------------------
	public void doLogin(User user){
		if( socketId==null ){
			socketId = work.startConnect("127.0.0.1", 1090);
		}
		String cmd = String.format("{op:login,user:{name:%s,pass:%s}}",
				user.getName(), user.getPass() );
		work.send( socketId, cmd );
	}
	public void doRegister(User user){
		if( socketId==null ){
			socketId = work.startConnect("127.0.0.1", 1090);
		}
		String cmd = String.format("{op:register,user:{%s}}", user.toString() );
		work.send( socketId, cmd );
	}
	public void doSend(String target, String message){
		String line = String.format( "{op:send,content:{msg:%s,target:%s}}", 
						message, target );
		work.send( socketId, line );        //(1) socketId: [自已的 SocketId]
	}
	public User getUser(String socketId){
		return users.get(socketId);
	}
	public ImageIcon getUserHead( String socketId ){
		System.out.println( "[Client] 拿  User: "+ socketId );
		User user = users.get( socketId );
		if( user!=null ){
			return user.getHead();
		}
		System.out.println( "[Client] 拿不到  ImageIcon" );
		return null;
	}
	@Override
	public void closeFrame(String socketId) {
		//[1] 通知 listFrame 处理窗体关闭。
		listFrame.removeChatFrame( socketId );
	}
	//---------------------------- EngineCallBack 回调方法 [END]  ---------------------------
	

	@Override
	public void onCreatedServer(boolean ret) { }
	@Override
	public void onAccepted() { }

	@Override
	public void onConnectEvent(boolean ret) {
		String retStr = (ret) ? "成功" : "失败";
		prt("连接服务器 ["+ retStr +"]");
	}

	@Override
	public void onReceived(byte[] buff, int size, String socketId) {
		String line = new String(buff,0,size);
		prt( "收到数据 : "+ line );
		Command cmd = CmdParser.parseServerCommand(line);
		String selfId = null;
		User curUser = null;
		switch( cmd.op ){
			case "register":
				prt( "服务器返回  register : "+ cmd.result );
			break;
			case "login":
				//[1] 如果 result == "yes"
				if( cmd.result.equals("yes") ){
					//[2] 获取服务器返回的  SocketId。
					selfId = cmd.data.get("socketId");
					//[3] 拷贝一个新的键值对: 新 ID <---> 原来 Socket ..
					work.copySocket( this.socketId, selfId );
					//[4] 更新当前所记录  SocketId.
					this.socketId = selfId;
					//[5] 将服务器返回信息来生成  User 对象( 你自己  )。
					curUser = CmdParser.makeUser( cmd.data );
					//[6] 关闭登陆界面
					loginFrame.dispose();
					//[7] 打开 ListFrame (new 出来)。
					listFrame = new ListFrame( this, curUser );    // this: 代表  ClientEngine 当前的对象
				}else{
					loginFrame.showMessage("登陆失败");
				}
			break;
			case "getList":   //获取到列表  ..
				//[1] 获取  cmd 中的 users。
				users = cmd.users;
				//[2] 从 Map [users] 中移除自己的 User 对象。
				users.remove( this.socketId );    //[3] 从 Map 当中清除自己的 信息。
				System.out.println( "[新 Users] "+ users );
				//[3] 在  listFrame 中显示用户信息。
				listFrame.showList( users );
			break;
			case "message":   //接收到客户端发来的消息 ..
				String msg = cmd.data.get("msg");
				String targetId = cmd.data.get("from");
				prt("收到 ["+ targetId +"] 的消息 ["+ msg +"]");
				listFrame.showMessage(targetId, msg);
			break;
		}
	}

	private void prtUsers() {
		int id = 0;
		for( String socketId : users.keySet() ){
			id ++;
			User user = users.get( socketId );
			System.out.printf("[%d] [%s] [%s]\n", 
					id, user.getNickName(), socketId );
		}
		prt("用户列表已更新, 可以选人发送消息  ...");
	}

	@Override
	public void onReceivedFailed() { }

	public void prt(Object obj) {
		System.out.println("[CLIENT] "+ obj);
	}
	
}
