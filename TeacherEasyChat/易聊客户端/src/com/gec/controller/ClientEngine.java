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

//[1] ʵ����һ���ӿڡ�

public class ClientEngine 
	implements MsgCallBack, EngineCallBack {

	NetWork work = new NetWork(this);
	String socketId;           //����ͻ��˱�������� SocketId
	Map<String,User> users;    //�������ߵ��û���Ϣ ..
	
	LoginFrame loginFrame;
	ListFrame listFrame;
	
	public void startGame(){
		loginFrame = new LoginFrame( this );
	}
	
	//[1] EngineCallBack �ص�����   -----------------------------------------------------
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
		work.send( socketId, line );        //(1) socketId: [���ѵ� SocketId]
	}
	public User getUser(String socketId){
		return users.get(socketId);
	}
	public ImageIcon getUserHead( String socketId ){
		System.out.println( "[Client] ��  User: "+ socketId );
		User user = users.get( socketId );
		if( user!=null ){
			return user.getHead();
		}
		System.out.println( "[Client] �ò���  ImageIcon" );
		return null;
	}
	@Override
	public void closeFrame(String socketId) {
		//[1] ֪ͨ listFrame ������رա�
		listFrame.removeChatFrame( socketId );
	}
	//---------------------------- EngineCallBack �ص����� [END]  ---------------------------
	

	@Override
	public void onCreatedServer(boolean ret) { }
	@Override
	public void onAccepted() { }

	@Override
	public void onConnectEvent(boolean ret) {
		String retStr = (ret) ? "�ɹ�" : "ʧ��";
		prt("���ӷ����� ["+ retStr +"]");
	}

	@Override
	public void onReceived(byte[] buff, int size, String socketId) {
		String line = new String(buff,0,size);
		prt( "�յ����� : "+ line );
		Command cmd = CmdParser.parseServerCommand(line);
		String selfId = null;
		User curUser = null;
		switch( cmd.op ){
			case "register":
				prt( "����������  register : "+ cmd.result );
			break;
			case "login":
				//[1] ��� result == "yes"
				if( cmd.result.equals("yes") ){
					//[2] ��ȡ���������ص�  SocketId��
					selfId = cmd.data.get("socketId");
					//[3] ����һ���µļ�ֵ��: �� ID <---> ԭ�� Socket ..
					work.copySocket( this.socketId, selfId );
					//[4] ���µ�ǰ����¼  SocketId.
					this.socketId = selfId;
					//[5] ��������������Ϣ������  User ����( ���Լ�  )��
					curUser = CmdParser.makeUser( cmd.data );
					//[6] �رյ�½����
					loginFrame.dispose();
					//[7] �� ListFrame (new ����)��
					listFrame = new ListFrame( this, curUser );    // this: ����  ClientEngine ��ǰ�Ķ���
				}else{
					loginFrame.showMessage("��½ʧ��");
				}
			break;
			case "getList":   //��ȡ���б�  ..
				//[1] ��ȡ  cmd �е� users��
				users = cmd.users;
				//[2] �� Map [users] ���Ƴ��Լ��� User ����
				users.remove( this.socketId );    //[3] �� Map ��������Լ��� ��Ϣ��
				System.out.println( "[�� Users] "+ users );
				//[3] ��  listFrame ����ʾ�û���Ϣ��
				listFrame.showList( users );
			break;
			case "message":   //���յ��ͻ��˷�������Ϣ ..
				String msg = cmd.data.get("msg");
				String targetId = cmd.data.get("from");
				prt("�յ� ["+ targetId +"] ����Ϣ ["+ msg +"]");
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
		prt("�û��б��Ѹ���, ����ѡ�˷�����Ϣ  ...");
	}

	@Override
	public void onReceivedFailed() { }

	public void prt(Object obj) {
		System.out.println("[CLIENT] "+ obj);
	}
	
}
