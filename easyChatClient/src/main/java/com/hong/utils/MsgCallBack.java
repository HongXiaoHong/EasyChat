package com.hong.utils;

public interface MsgCallBack {

	//创建服务器是否成功
	void onCreateServer(boolean flag);
	//当有客户端连接起来
	void onAccepted();
	//当服务器成功或是失败
	void OnConntextEvent(boolean flag);
	//接受客服端信息
	void onReveived(byte[] buff);
	//接受数据发生异常
	void onReveiveFailed();
	void onReceived(byte[] buff, int count, String socketID);
}
