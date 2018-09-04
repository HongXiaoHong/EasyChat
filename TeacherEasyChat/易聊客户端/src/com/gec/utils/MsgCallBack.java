package com.gec.utils;

public interface MsgCallBack {
	void onCreatedServer(boolean ret);
	void onAccepted();
	
	void onConnectEvent(boolean ret);
	
	void onReceived(byte[] buff,int size,String socketId);
	void onReceivedFailed();
}
